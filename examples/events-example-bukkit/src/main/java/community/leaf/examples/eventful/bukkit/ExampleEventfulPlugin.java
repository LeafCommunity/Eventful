package community.leaf.examples.eventful.bukkit;

import community.leaf.eventful.bukkit.EventSource;
import community.leaf.eventful.bukkit.Events;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import pl.tlinkowski.annotation.basic.NullOr;

public class ExampleEventfulPlugin extends JavaPlugin implements EventSource
{
    @Override
    public void onEnable()
    {
        Events.resolve().on(PlayerInteractEvent.class, event -> {
            @NullOr Block block = event.getClickedBlock();
            if (block == null) { return; }
            event.getPlayer().sendMessage("You interacted with: " + block.getType());
        });
    }
    
    @Override
    public Plugin plugin() { return this; }
}
