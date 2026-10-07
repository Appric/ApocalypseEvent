package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import approt.apocalypseEvent.InfectionFunctions;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class PlayerMoveListener implements Listener {

    // Connect to the main and InfectedFunctions class
    private final ApocalypseEvent plugin;
    private final InfectionFunctions infectionFunctions;

    public PlayerMoveListener(ApocalypseEvent plugin, InfectionFunctions infectionFunctions) {
        this.plugin = plugin;
        this.infectionFunctions = infectionFunctions;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Check if the plugin is enabled
        if (!plugin.enabled)
            return;

        // Get locations
        Location from = event.getFrom();
        Location to = event.getTo();

        // Check if it was not a new block
        if (to == null || (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()))
            return;
        // Get the player
        Player player = event.getPlayer();

        // Check if they entered a new biome
        Biome fromBiome = from.getBlock().getBiome();
        Biome toBiome = to.getBlock().getBiome();
        if (fromBiome != toBiome) {
            // Check if they entered a cold biome from a not cold biome
            if (plugin.freezingBiomes.contains(toBiome) && !plugin.freezingBiomes.contains(fromBiome)) {
                plugin.applyPlayerFreeze(player);
            }
        }

        // Check if they are in water and call the water effect repeating task if they don't already have it
        if(player.isInWater() && !plugin.waterEffectTasks.containsKey(player.getUniqueId()))
        {
            plugin.applyWaterEffects(player);
        }
    }
}
