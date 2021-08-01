package community.leaf.examples.eventful.bukkit;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ExampleEvent extends Event implements Cancellable
{
    private final CommandSender sender;
    
    public ExampleEvent(CommandSender sender)
    {
        this.sender = sender;
    }
    
    public CommandSender getSender()
    {
        return sender;
    }
    
    // - - - - - - Cancellable Boilerplate - - - - - -
    
    private boolean cancelled = false;
    
    @Override
    public boolean isCancelled() { return cancelled; }
    
    @Override
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    
    // - - - - - - HandlerList Boilerplate - - - - - -
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    public static HandlerList getHandlerList() { return HANDLERS; }
    
    @Override
    public HandlerList getHandlers() { return HANDLERS; }
}
