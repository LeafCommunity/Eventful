package community.leaf.examples.eventful.bukkit;

import community.leaf.eventful.bukkit.BukkitEventSource;
import community.leaf.eventful.bukkit.CancellationPolicy;
import community.leaf.eventful.bukkit.Events;
import community.leaf.eventful.bukkit.ListenerOrder;
import community.leaf.eventful.bukkit.annotations.EventListener;
import community.leaf.eventful.bukkit.annotations.IfCancelled;
import community.leaf.eventful.bukkit.events.UncaughtEventExceptionEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import pl.tlinkowski.annotation.basic.NullOr;

public class ExampleEventfulPlugin extends JavaPlugin implements BukkitEventSource, Listener
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
        
        events().on(UncaughtEventExceptionEvent.class, event -> {
            getLogger().warning("Something went wrong in event: " + event.getEventName() + " (" + event + ")");
            event.getException().printStackTrace();
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
    
    @EventListener(ListenerOrder.FIRST)
    @IfCancelled(CancellationPolicy.IGNORE)
    public void onPlayerSneak(PlayerToggleSneakEvent event)
    {
        if (event.isSneaking())
        {
            event.getPlayer().spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                new ComponentBuilder("Sneaky...").italic(true).color(ChatColor.GRAY).create()
            );
        }
        
        if (Math.random() < 0.10)
        {
            throw new RuntimeException("Oopsies");
        }
    }
}
