package community.leaf.eventful.bukkit.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Objects;

public class UncaughtEventExceptionEvent extends Event
{
    private final Event event;
    private final Listener listener;
    private final RuntimeException exception;
    
    public UncaughtEventExceptionEvent(Event event, Listener listener, RuntimeException exception)
    {
        super(Objects.requireNonNull(event, "event").isAsynchronous());
        this.event = event;
        this.listener = Objects.requireNonNull(listener, "listener");
        this.exception = Objects.requireNonNull(exception, "exception");
    }
    
    public Event getEvent()
    {
        return event;
    }
    
    public Listener getListener()
    {
        return listener;
    }
    
    public RuntimeException getException()
    {
        return exception;
    }
    
    // - - - - - - HandlerList Boilerplate - - - - - -
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    public static HandlerList getHandlerList() { return HANDLERS; }
    
    @Override
    public HandlerList getHandlers() { return HANDLERS; }
}
