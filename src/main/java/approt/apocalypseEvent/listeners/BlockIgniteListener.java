package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

public class BlockIgniteListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;
    public BlockIgniteListener(ApocalypseEvent plugin) {this.plugin = plugin;}

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event)
    {
        // Check if the plugin and lightning storms are enabled
        if(!plugin.enabled || !plugin.lightningStorms)
            return;

        // Prevents lightning from leaving fire
        if(event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING)
            event.setCancelled(true);
    }
}
