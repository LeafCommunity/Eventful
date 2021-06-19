package community.leaf.examples.eventful.bukkit;

import community.leaf.eventful.bukkit.EventSource;
import community.leaf.eventful.bukkit.Events;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import pl.tlinkowski.annotation.basic.NullOr;

public class ExampleEventfulPlugin extends JavaPlugin implements EventSource, Listener
{
    @Override
    public void onEnable()
    {
        Events.resolve().on(PlayerInteractEvent.class, event -> {
            @NullOr Block block = event.getClickedBlock();
            if (block == null) { return; }
            event.getPlayer().sendMessage("You interacted with: " + block.getType());
        });
        
        events().on(PlayerDropItemEvent.class).ignoringCancelled().early().listener(event -> {
            event.getPlayer().sendMessage("You dropped: " + event.getItemDrop().getItemStack().getType());
        });
        
        events().on(ExampleEvent.class).ignoringCancelled().last().listener(event -> {
            event.getSender().sendMessage(ChatColor.LIGHT_PURPLE + "Have some dessert!");
        });
        
        events().register(this);
    }
    
    @Override
    public Plugin plugin() { return this; }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (events().call(new ExampleEvent(sender)).isCancelled())
        {
            sender.sendMessage("Result: event was cancelled.");
        }
        else
        {
            sender.sendMessage("Result: event was not cancelled.");
        }
        
        return true;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServeAppetizer(ExampleEvent event)
    {
        event.getSender().sendMessage(ChatColor.YELLOW + "Here's an appetizer to get you started.");
        
        if (Math.random() < 0.25)
        {
            event.getSender().sendMessage(ChatColor.GREEN + "You now feel sick...");
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onServeEntree(ExampleEvent event)
    {
        event.getSender().sendMessage(ChatColor.BLUE + "Your entrée is served.");
        
        if (Math.random() < 0.50)
        {
            event.getSender().sendMessage(ChatColor.DARK_GRAY + "You're full!");
            event.setCancelled(true);
        }
    }
}
