/*
 * Copyright © 2021, RezzedUp <http://github.com/LeafCommunity/Eventful>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.eventful.bukkit;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

@FunctionalInterface
public interface Events extends EventDispatcher
{
    static Events resolve()
    {
        Plugin plugin = EventsImpl.resolvePluginByStackTrace();
        return () -> plugin;
    }
    
    static EventDispatcher dispatcher()
    {
        return EventsImpl::dispatch;
    }
    
    Plugin plugin();
    
    @Override
    default <E extends Event> E call(E event)
    {
        Objects.requireNonNull(event, "event");
        plugin().getServer().getPluginManager().callEvent(event);
        return event;
    }
    
    default <L extends Listener> L register(L listener)
    {
        Objects.requireNonNull(listener, "listener");
        plugin().getServer().getPluginManager().registerEvents(listener, plugin());
        return listener;
    }
    
    @SuppressWarnings("unchecked")
    private static void handle(Listener listener, Event event)
    {
        ((EventConsumer<Event>) listener).accept(event);
    }
    
    default <E extends Event> void on(Class<E> event, EventPriority priority, boolean ignoredCancelled, EventConsumer<E> listener)
    {
        Objects.requireNonNull(event, "event");
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(listener, "listener");
        
        plugin().getServer().getPluginManager().registerEvent(
            event, listener, priority, Events::handle, plugin(), ignoredCancelled
        );
    }
    
    default <E extends Event> void on(Class<E> event, EventPriority priority, EventConsumer<E> listener)
    {
        on(event, priority, true, listener);
    }
    
    default <E extends Event> void on(Class<E> event, EventConsumer<E> listener)
    {
        on(event, EventPriority.NORMAL, listener);
    }
    
    default <E extends Event> Builder<E> on(Class<E> event)
    {
        return new EventsImpl.Builder<>(this, event);
    }
    
    interface Builder<E extends Event>
    {
        Builder<E> priority(EventPriority priority);
        
        Builder<E> ignoreCancelled(boolean ignoreCancelled);
        
        void listener(EventConsumer<E> listener);
    }
}
