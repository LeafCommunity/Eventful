/*
 * Copyright © 2021, RezzedUp <http://github.com/LeafCommunity/Eventful>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.eventful.bukkit;

import community.leaf.eventful.bukkit.annotations.EventListener;
import community.leaf.eventful.bukkit.annotations.CancelledEvents;
import community.leaf.eventful.bukkit.events.UncaughtEventExceptionEvent;
import org.bukkit.Bukkit;
import org.bukkit.Warning;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import pl.tlinkowski.annotation.basic.NullOr;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class EventsImpl
{
    private EventsImpl() { throw new UnsupportedOperationException(); }
    
    private static final String PACKAGE = EventsImpl.class.getPackageName();
    
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    
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
    
    private static EventExecutor handle(ExceptionalExecutor executor)
    {
        return (listener, event) ->
        {
            try { executor.execute(listener, event); }
            catch (Error error) { throw error; } // Rethrow errors
            catch (Throwable uncaught)
            {
                int handlers = UncaughtEventExceptionEvent.getHandlerList().getRegisteredListeners().length;
                if (handlers <= 0 || event instanceof UncaughtEventExceptionEvent) { throw new EventException(uncaught); }
                dispatch(new UncaughtEventExceptionEvent(event, listener, uncaught));
            }
        };
    }
    
    private static <E extends Event> void register(
        Plugin plugin,
        Class<E> eventType,
        Listener listener,
        EventPriority priority,
        boolean ignoredCancelled,
        ExceptionalExecutor executor
    ) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(listener, "listener");
        Objects.requireNonNull(executor, "executor");
        
        checkThenWarnIfDeprecatedEvent(plugin, eventType);
        
        plugin.getServer().getPluginManager().registerEvent(
            eventType, listener, priority, handle(executor), plugin, ignoredCancelled
        );
    }
    
    @SuppressWarnings("ConstantConditions")
    private static <A extends Annotation> Optional<A> annotation(AnnotatedElement annotated, Class<A> type)
    {
        return Optional.ofNullable(annotated.getAnnotation(type));
    }
    
    private static void checkThenWarnIfDeprecatedEvent(Plugin plugin, Class<? extends Event> eventType)
    {
        for (Class<?> clazz = eventType; Event.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass())
        {
            if (!clazz.isAnnotationPresent(Deprecated.class)) { continue; }
    
            Warning.WarningState warningState = plugin.getServer().getWarningState();
            Optional<Warning> warning = annotation(clazz, Warning.class);
            
            // Differs from bukkit: always print warnings unless explicitly turned off.
            if (warningState == Warning.WarningState.OFF) { break; }
            
            plugin.getLogger().log(
                Level.WARNING,
                String.format(
                    "%s has registered a listener for %s, but the event is deprecated (%s). %s.",
                    plugin.getDescription().getFullName(),
                    eventType.getSimpleName(),
                    clazz.getName(),
                    warning.map(Warning::reason)
                        .filter(Predicate.not(String::isBlank))
                        .orElse("Server performance will be affected")
                ),
                (warningState == Warning.WarningState.ON) ? new AuthorNagException(null) : null
            );
            
            return;
        }
    }
    
    @SuppressWarnings("unchecked")
    static <E extends Event> void registerEventConsumer(
        Plugin plugin,
        Class<E> eventType,
        EventPriority priority,
        boolean ignoredCancelled,
        EventConsumer<E> listener
    ) {
        register(plugin, eventType, listener, priority, ignoredCancelled, (li, ev) -> {
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
        
        for (Method method : methods)
        {
            try { registerMethod(plugin, listener, method); }
            catch (RuntimeException e)
            {
                plugin.getLogger().log(
                    Level.SEVERE,
                    String.format(
                        "%s could not register event listener method \"%s\" in %s",
                        plugin.getDescription().getFullName(),
                        method.toGenericString(),
                        listener.getClass()
                    ),
                    e
                );
            }
        }
    }
    
    private static void registerMethod(Plugin plugin, Listener listener, Method method)
    {
        @NullOr EventHandler meta = resolveAnnotation(listener, method);
        if (meta == null) { return; }
        
        @NullOr Class<?> param = (method.getParameterCount() != 1) ? null : method.getParameterTypes()[0];
        @NullOr Class<? extends Event> eventType = (param == null) ? null : param.asSubclass(Event.class);
        
        if (eventType == null)
        {
            plugin.getLogger().severe(String.format(
                "%s attempted to register an invalid event listener method signature \"%s\" in %s",
                plugin.getDescription().getFullName(),
                method.toGenericString(),
                listener.getClass()
            ));
            return;
        }
        
        method.setAccessible(true);
        MethodHandle handle;
        
        try { handle = LOOKUP.unreflect(method); }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
        
        register(plugin, eventType, listener, meta.priority(), meta.ignoreCancelled(), (li, ev) -> {
            if (eventType.isAssignableFrom(ev.getClass())) { handle.invoke(li, ev); }
        });
    }
    
    private static @NullOr EventHandler resolveAnnotation(Listener listener, Method method)
    {
        return annotation(method, EventHandler.class).orElseGet(() ->
        {
            @NullOr ListenerOrder order =
                annotation(method, EventListener.class)
                    .map(EventListener::value)
                    .orElse(null);
            
            if (order == null) { return null; }
            
            Class<?> clazz = listener.getClass();
            
            CancellationPolicy policy =
                Stream.of(method, clazz, clazz.getPackage())
                    .flatMap(element -> annotation(element, CancelledEvents.class).stream())
                    .map(CancelledEvents::value)
                    .findFirst()
                    .orElse(CancellationPolicy.ACCEPT);
            
            return new EventHandler()
            {
                @Override
                public Class<? extends Annotation> annotationType() { return EventHandler.class; }
                
                @Override
                public EventPriority priority() { return order.priority(); }
                
                @Override
                public boolean ignoreCancelled() { return policy.ignoresCancelledEvents(); }
            };
        });
    }
    
    @FunctionalInterface
    interface ExceptionalExecutor
    {
        void execute(Listener listener, Event event) throws Throwable;
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
        public Builder<E> priority(ListenerOrder order)
        {
            Objects.requireNonNull(order, "order");
            this.priority = order.priority();
            return this;
        }
    
        @Override
        public Events.Builder<E> cancelled(CancellationPolicy policy)
        {
            Objects.requireNonNull(policy, "policy");
            this.ignoreCancelled = policy.ignoresCancelledEvents();
            return this;
        }
    
        @Override
        public void listener(EventConsumer<E> listener)
        {
            registerEventConsumer(plugin, event, priority, ignoreCancelled, listener);
        }
    }
}
