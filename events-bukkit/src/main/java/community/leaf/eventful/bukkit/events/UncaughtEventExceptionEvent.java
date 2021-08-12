/*
 * Copyright © 2021, RezzedUp <http://github.com/LeafCommunity/Eventful>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.eventful.bukkit.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Objects;

/**
 * An event that gets called when an uncaught exception
 * occurs while handling another event. Use this to customize
 * event exception handling.
 */
public class UncaughtEventExceptionEvent extends Event
{
    private final Event event;
    private final Listener listener;
    private final Throwable exception;
    
    /**
     * Constructs.
     *
     * @param event         the exceptional event
     * @param listener      the listener that caused the exception
     * @param exception     the uncaught exception
     */
    public UncaughtEventExceptionEvent(Event event, Listener listener, Throwable exception)
    {
        super(Objects.requireNonNull(event, "event").isAsynchronous());
        this.event = event;
        this.listener = Objects.requireNonNull(listener, "listener");
        this.exception = Objects.requireNonNull(exception, "exception");
    }
    
    /**
     * Gets the event that was being handled.
     *
     * @return  the exceptional event
     */
    public Event getEvent()
    {
        return event;
    }
    
    /**
     * Gets the listener the caused the exception.
     *
     * @return  the exception-generating listener
     */
    public Listener getListener()
    {
        return listener;
    }
    
    /**
     * Gets the uncaught exception.
     *
     * @return  the uncaught exception
     */
    public Throwable getException()
    {
        return exception;
    }
    
    // - - - - - - HandlerList Boilerplate - - - - - -
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    public static HandlerList getHandlerList() { return HANDLERS; }
    
    @Override
    public HandlerList getHandlers() { return HANDLERS; }
}
