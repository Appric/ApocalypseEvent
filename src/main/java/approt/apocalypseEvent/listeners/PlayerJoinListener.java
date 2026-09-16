package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;

    public PlayerJoinListener(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        // Check if the plugin is enabled
        if (!plugin.enabled)
            return;

        // Declare player
        Player player = event.getPlayer();

        // Check if the player joining is in a cold biome and needs freezing ticks
        if(plugin.freezingBiomes.contains(player.getLocation().getBlock().getBiome()))
            plugin.applyPlayerFreeze(player);
    }
}
