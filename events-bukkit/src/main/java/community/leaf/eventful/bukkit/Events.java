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
    
    default <E extends Event> void on(Class<E> event, EventPriority priority, boolean ignoredCancelled, EventConsumer<E> listener)
    {
        Objects.requireNonNull(event, "event");
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(listener, "listener");
        
        plugin().getServer().getPluginManager().registerEvent(
            event, listener, priority, EventsImpl::handle, plugin(), ignoredCancelled
        );
    }
    
    default <E extends Event> void on(Class<E> event, EventPriority priority, EventConsumer<E> listener)
    {
        on(event, priority, false, listener);
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
        
        default Builder<E> first() { return priority(EventPriority.LOWEST); }
        
        default Builder<E> early() { return priority(EventPriority.LOW); }
        
        default Builder<E> normal() { return priority(EventPriority.NORMAL); }
        
        default Builder<E> later() { return priority(EventPriority.HIGH); }
        
        default Builder<E> last() { return priority(EventPriority.HIGHEST); }
        
        default Builder<E> monitor() { return priority(EventPriority.MONITOR); }
        
        default Builder<E> acceptingCancelled() { return ignoreCancelled(false); }
        
        default Builder<E> ignoringCancelled() { return ignoreCancelled(true); }
    }
}
