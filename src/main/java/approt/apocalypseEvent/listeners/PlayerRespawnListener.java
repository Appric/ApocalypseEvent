package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;

    public PlayerRespawnListener(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        // Check if the plugin is enabled
        if (!plugin.enabled)
            return;

        // Declare player
        Player player = event.getPlayer();

        // Check if the player respawning is respawning in a cold biome and needs freezing ticks
        if(plugin.freezingBiomes.contains(event.getRespawnLocation().getBlock().getBiome()))
            plugin.applyPlayerFreeze(player);
    }
}
