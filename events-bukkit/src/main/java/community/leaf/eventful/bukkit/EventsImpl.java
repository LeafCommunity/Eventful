/*
 * Copyright © 2021, RezzedUp <http://github.com/LeafCommunity/Eventful>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.eventful.bukkit;

import community.leaf.eventful.bukkit.annotations.EventListener;
import community.leaf.eventful.bukkit.annotations.IfCancelled;
import community.leaf.eventful.bukkit.events.UncaughtEventExceptionEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import pl.tlinkowski.annotation.basic.NullOr;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    
    static EventExecutor handle(EventExecutor executor)
    {
        return (listener, event) ->
        {
            try
            {
                executor.execute(listener, event);
            }
            catch (Error | EventException error)
            {
                // Rethrow errors / existing event exceptions...
                throw error;
            }
            catch (Throwable uncaught)
            {
                int handlers = UncaughtEventExceptionEvent.getHandlerList().getRegisteredListeners().length;
                if (handlers <= 0 || event instanceof UncaughtEventExceptionEvent) { throw new EventException(uncaught); }
                dispatch(new UncaughtEventExceptionEvent(event, listener, uncaught));
            }
        };
    }
    
    static <E extends Event> void registerListener(
        Plugin plugin,
        Class<E> eventType,
        Listener listener,
        EventPriority priority,
        boolean ignoredCancelled,
        EventExecutor executor
    ) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(listener, "listener");
        Objects.requireNonNull(executor, "executor");
        
        plugin.getServer().getPluginManager().registerEvent(
            eventType, listener, priority, handle(executor), plugin, ignoredCancelled
        );
    }
    
    @SuppressWarnings("unchecked")
    static <E extends Event> void registerEventConsumer(
        Plugin plugin,
        Class<E> eventType,
        EventPriority priority,
        boolean ignoredCancelled,
        EventConsumer<E> listener
    ) {
        registerListener(plugin, eventType, listener, priority, ignoredCancelled, (li, ev) -> {
            if (eventType.isAssignableFrom(ev.getClass())) { ((EventConsumer<E>) li).accept((E) ev); }
        });
    }
    
    static void registerMethods(Plugin plugin, Listener listener)
    {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(listener, "listener");
        
        Class<?> clazz = listener.getClass();
    
        Set<Method> methods =
            Stream.concat(Arrays.stream(clazz.getMethods()), Arrays.stream(clazz.getDeclaredMethods()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        
        for (Method method : methods) {registerMethod(plugin, listener, method);}
    }
    
    static void registerMethod(Plugin plugin, Listener listener, Method method)
    {
        @NullOr ListenerMeta meta = resolveMeta(listener, method);
        if (meta == null) { return; }
        
        @NullOr Class<?> param = (method.getParameterCount() != 1) ? null : method.getParameterTypes()[0];
        @NullOr Class<? extends Event> eventType = (param == null) ? null : param.asSubclass(Event.class);
        
        if (eventType == null)
        {
            plugin.getLogger().severe(
                plugin.getDescription().getFullName() + " attempted to register an invalid event " +
                "handler (listener) method signature \"" + method.toGenericString() + "\" in " + listener.getClass()
            );
            return;
        }
        
        method.setAccessible(true);
        
        registerListener(plugin, eventType, listener, meta.priority, meta.ignoreCancelled, (li, ev) -> {
            if (!eventType.isAssignableFrom(ev.getClass())) { return; }
            try { method.invoke(li, ev); }
            catch (IllegalAccessException | InvocationTargetException e) { throw new EventException(e); }
        });
    }
    
    @SuppressWarnings("ConstantConditions")
    static @NullOr ListenerMeta resolveMeta(Listener listener, Method method)
    {
        @NullOr EventHandler eventHandler = method.getAnnotation(EventHandler.class);
        if (eventHandler != null)
        {
            return new ListenerMeta(eventHandler.priority(), eventHandler.ignoreCancelled());
        }
        
        @NullOr EventListener eventListener = method.getAnnotation(EventListener.class);
        if (eventListener == null) { return null; }
        
        Class<?> clazz = listener.getClass();
        CancellationPolicy policy = CancellationPolicy.ACCEPT;
        
        for (AnnotatedElement annotated : List.of(method, clazz, clazz.getPackage()))
        {
            @NullOr IfCancelled ifCancelled = annotated.getAnnotation(IfCancelled.class);
            if (ifCancelled != null)
            {
                policy = ifCancelled.value();
                break;
            }
        }
        
        return new ListenerMeta(eventListener.value().priority(), policy.ignoresCancelledEvents());
    }
    
    private static class ListenerMeta
    {
        final EventPriority priority;
        final boolean ignoreCancelled;
        
        ListenerMeta(EventPriority priority, boolean ignoreCancelled)
        {
            this.priority = priority;
            this.ignoreCancelled = ignoreCancelled;
        }
    }
    
    static final class Builder<E extends Event> implements Events.Builder<E>
    {
        private final Plugin plugin;
        private final Class<E> event;
        
        private EventPriority priority = EventPriority.NORMAL;
        private boolean ignoreCancelled = false;
        
        Builder(Plugin plugin, Class<E> event)
        {
            this.plugin = plugin;
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
            registerEventConsumer(plugin, event, priority, ignoreCancelled, listener);
        }
    }
}
