/*
 * Copyright © 2021, RezzedUp <http://github.com/LeafCommunity/Eventful>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.eventful.bukkit;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

/**
 * Utilities for calling and registering Bukkit events.
 */
@FunctionalInterface
public interface Events extends EventDispatcher
{
    /**
     * Creates a new {@code Events} instance by
     * resolving plugin classes on the current call
     * stack. The first plugin found on the call
     * stack will be used for event registration.
     *
     * <p><b>Note:</b> a stacktrace will be generated
     * every time this method is invoked, so consider
     * reusing instances rather than constantly
     * re-resolving them.</p>
     *
     * @return  a new instance that registers events
     *          with the plugin resolved from the
     *          current call stack
     */
    static Events resolve()
    {
        Plugin plugin = EventsImpl.resolvePluginByStackTrace();
        return () -> plugin;
    }
    
    /**
     * Static helper for calling Bukkit events.
     *
     * @return  a Bukkit event dispatcher
     */
    static EventDispatcher dispatcher()
    {
        return EventsImpl::dispatch;
    }
    
    /**
     * Gets the plugin used for registering events.
     *
     * @return  a plugin
     */
    Plugin plugin();
    
    @Override // documented in supertype
    default <E extends Event> E call(E event)
    {
        Objects.requireNonNull(event, "event");
        plugin().getServer().getPluginManager().callEvent(event);
        return event;
    }
    
    /**
     * Registers the provided event listener
     * then returns it.
     *
     * @param listener  the event listener
     * @param <L>       specific listener type
     *
     * @return  the registered listener
     */
    @SuppressWarnings("UnusedReturnValue")
    default <L extends Listener> L register(L listener)
    {
        Objects.requireNonNull(listener, "listener");
        plugin().getServer().getPluginManager().registerEvents(listener, plugin());
        return listener;
    }
    
    /**
     * Registers the provided event consumer.
     *
     * @param event             the event type
     * @param priority          the priority
     * @param ignoredCancelled  whether to ignore cancelled events or not
     * @param listener          the event handler
     * @param <E>               event type
     *
     * @see EventHandler#ignoreCancelled()
     */
    default <E extends Event> void on(Class<E> event, EventPriority priority, boolean ignoredCancelled, EventConsumer<E> listener)
    {
        Objects.requireNonNull(event, "event");
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(listener, "listener");
        
        plugin().getServer().getPluginManager().registerEvent(
            event, listener, priority, EventsImpl::handle, plugin(), ignoredCancelled
        );
    }
    
    /**
     * Registers the provided event consumer,
     * accepting all events whether cancelled or not.
     *
     * @param event     the event type
     * @param priority  the priority
     * @param listener  the event handler
     * @param <E>       event type
     */
    default <E extends Event> void on(Class<E> event, EventPriority priority, EventConsumer<E> listener)
    {
        on(event, priority, false, listener);
    }
    
    /**
     * Registers the provided event consumer
     * at {@code NORMAL} priority, accepting all
     * events whether cancelled or not.
     *
     * @param event     the event type
     * @param listener  the event handler
     * @param <E>       event type
     */
    default <E extends Event> void on(Class<E> event, EventConsumer<E> listener)
    {
        on(event, EventPriority.NORMAL, listener);
    }
    
    /**
     * Creates a new event registration builder for
     * the provided event type.
     *
     * @param event     the event type
     * @param <E>       event type
     */
    default <E extends Event> Builder<E> on(Class<E> event)
    {
        return new EventsImpl.Builder<>(this, event);
    }
    
    /**
     * Event registration builder.
     *
     * @param <E>   event type
     */
    interface Builder<E extends Event>
    {
        /**
         * Sets the event priority.
         *
         * @param priority  the priority
         *
         * @return  the builder
         *          (for method chaining)
         */
        Builder<E> priority(EventPriority priority);
    
        /**
         * Sets whether cancelled events are ignored by
         * the listener or not.
         *
         * @param ignoreCancelled   whether to ignore cancelled events or not
         *
         * @return  the builder
         *          (for method chaining)
         *
         * @see EventHandler#ignoreCancelled()
         */
        Builder<E> ignoreCancelled(boolean ignoreCancelled);
    
        /**
         * Registers the provided listener with the
         * builder's previously specified settings.
         *
         * @param listener  event consumer
         */
        void listener(EventConsumer<E> listener);
        
        default Builder<E> priority(ListenerOrder order) { return priority(order.priority()); }
        
        default Builder<E> cancelled(CancellationPolicy policy)
        {
            return ignoreCancelled(policy.ignoresCancelledEvents());
        }
        
        /**
         * Sets the priority to {@link ListenerOrder#FIRST},
         * which gets called first.
         *
         * @return  the builder
         *          (for method chaining)
         */
        default Builder<E> first() { return priority(ListenerOrder.FIRST); }
        
        /**
         * Sets the priority to {@link ListenerOrder#EARLY},
         * which gets called earlier than most other priorities.
         *
         * @return  the builder
         *          (for method chaining)
         */
        default Builder<E> early() { return priority(ListenerOrder.EARLY); }
        
        /**
         * Sets the priority to {@link ListenerOrder#NORMAL},
         * which is the default priority for event listeners.
         *
         * @return  the builder
         *          (for method chaining)
         */
        default Builder<E> normal() { return priority(ListenerOrder.NORMAL); }
        
        /**
         * Sets the priority to {@link ListenerOrder#LATE},
         * which gets called after most other priorities.
         *
         * @return  the builder
         *          (for method chaining)
         */
        default Builder<E> late() { return priority(ListenerOrder.LATE); }
        
        /**
         * Sets the priority to {@link ListenerOrder#LAST},
         * which gets called last.
         *
         * @return  the builder
         *          (for method chaining)
         */
        default Builder<E> last() { return priority(ListenerOrder.LAST); }
        
        /**
         * Sets the priority to {@link ListenerOrder#MONITOR},
         * which <i>truly</i> gets called last (because it's meant
         * to <i>monitor</i> the outcome of previous listeners).
         *
         * @return  the builder
         *          (for method chaining)
         */
        default Builder<E> monitor() { return priority(ListenerOrder.MONITOR); }
    
        /**
         * Makes the event listener accept all events,
         * regardless of its cancellation status.
         *
         * @return  the builder
         *          (for method chaining)
         */
        default Builder<E> acceptingCancelled() { return cancelled(CancellationPolicy.ACCEPT); }
    
        /**
         * Makes the event listener ignore events
         * if they're cancelled.
         *
         * @return  the builder
         *          (for method chaining)
         */
        default Builder<E> ignoringCancelled() { return cancelled(CancellationPolicy.IGNORE); }
    }
}
