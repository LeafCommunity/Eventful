/*
 * Copyright © 2021, RezzedUp <http://github.com/LeafCommunity/Eventful/events-bukkit>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.eventful.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

final class EventsImpl
{
    private EventsImpl() { throw new UnsupportedOperationException(); }
    
    private static final String PACKAGE = EventsImpl.class.getPackageName();
    
    static JavaPlugin resolvePluginByStackTrace()
    {
        for (StackTraceElement element : Thread.currentThread().getStackTrace())
        {
            String name = element.getClassName();
            
            if (element.isNativeMethod()) { continue; }
            if (name.startsWith("java.")) { continue; }
            if (name.startsWith(PACKAGE)) { continue; }
            
            try { return JavaPlugin.getProvidingPlugin(Class.forName(element.getClassName())); }
            catch (Exception ignored) {}
        }
        
        throw new IllegalStateException("Unable to resolve plugin (no plugin class found in stack trace)");
    }
    
    static <E extends Event> E dispatch(E event)
    {
        Objects.requireNonNull(event, "event");
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }
    
    static final class Builder<E extends Event> implements Events.Builder<E>
    {
        private final Events events;
        private final Class<E> event;
        
        private EventPriority priority = EventPriority.NORMAL;
        private boolean ignoreCancelled = false;
        
        Builder(Events events, Class<E> event)
        {
            this.events = events;
            this.event = Objects.requireNonNull(event, "event");
        }
        
        @Override
        public Builder<E> priority(EventPriority priority)
        {
            this.priority = Objects.requireNonNull(priority);
            return this;
        }
        
        @Override
        public Builder<E> ignoreCancelled(boolean ignoreCancelled)
        {
            this.ignoreCancelled = ignoreCancelled;
            return this;
        }
        
        @Override
        public void listener(EventConsumer<E> listener)
        {
            events.on(event, priority, ignoreCancelled, listener);
        }
    }
}
