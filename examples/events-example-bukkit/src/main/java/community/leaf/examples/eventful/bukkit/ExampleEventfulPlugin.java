package community.leaf.examples.eventful.bukkit;

import community.leaf.eventful.bukkit.EventSource;
import community.leaf.eventful.bukkit.Events;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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
        
        events().register(this);
    }
    
    @Override
    public Plugin plugin() { return this; }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        event.getPlayer().sendMessage("Welcome back!");
    }
}
