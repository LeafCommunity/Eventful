package community.leaf.examples.eventful.bukkit;

import community.leaf.eventful.bukkit.BukkitEventSource;
import community.leaf.eventful.bukkit.CancellationPolicy;
import community.leaf.eventful.bukkit.Events;
import community.leaf.eventful.bukkit.ListenerOrder;
import community.leaf.eventful.bukkit.annotations.EventListener;
import community.leaf.eventful.bukkit.annotations.CancelledEvents;
import community.leaf.eventful.bukkit.events.UncaughtEventExceptionEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.logging.Level;

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
        
        events().on(PlayerDropItemEvent.class).rejectCancelled().early().listener(event -> {
            event.getPlayer().sendMessage("You dropped: " + event.getItemDrop().getItemStack().getType());
        });
        
        events().on(ExampleEvent.class).rejectCancelled().last().listener(event -> {
            event.getSender().sendMessage(ChatColor.LIGHT_PURPLE + "3: Have some dessert!");
        });
        
        events().on(PlayerPreLoginEvent.class, deprecated -> {
            getLogger().info(deprecated.getName() + " is joining. . .");
        });
        
        events().on(UncaughtEventExceptionEvent.class, event ->
        {
            Event problem = event.getEvent();
            Throwable exception = event.getException();
            
            getLogger().log(
                Level.SEVERE,
                "Something went wrong in event: " + problem.getEventName() + " (" + problem + ")",
                exception
            );
            
            getServer().getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("exceptions.notify"))
                .forEach(p -> p.sendMessage(
                    "Error: " + ChatColor.RED + "Something went wrong in event: " + problem.getEventName() + "\n" +
                    ChatColor.DARK_GRAY + exception.getClass().getSimpleName() + ": " + exception.getMessage()
                ));
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
            sender.sendMessage(ChatColor.DARK_GREEN + "Result: event was cancelled.");
        }
        else
        {
            sender.sendMessage(ChatColor.DARK_PURPLE + "Result: event was not cancelled.");
        }
        
        return true;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServeAppetizer(ExampleEvent event)
    {
        event.getSender().sendMessage(ChatColor.LIGHT_PURPLE + "1: Here's an appetizer to get you started.");
        
        if (Math.random() < 0.25)
        {
            event.getSender().sendMessage(ChatColor.GREEN + "X: You now feel sick...");
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onServeEntree(ExampleEvent event)
    {
        event.getSender().sendMessage(ChatColor.LIGHT_PURPLE + "2: Your entrée is served.");
        
        if (Math.random() < 0.50)
        {
            event.getSender().sendMessage(ChatColor.GREEN + "X: You're full!");
            event.setCancelled(true);
        }
    }
    
    @EventListener(ListenerOrder.FIRST)
    @CancelledEvents(CancellationPolicy.REJECT)
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
            throw new RuntimeException("Oopsies (example exception)");
        }
    }
    
    @EventListener(ListenerOrder.FIRST)
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        event.setMessage(event.getMessage().replace("->", "→").replace("<-", "←"));
    }
}
