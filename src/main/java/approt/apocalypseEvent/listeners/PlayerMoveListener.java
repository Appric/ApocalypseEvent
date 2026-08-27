package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

public class PlayerMoveListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;
    public PlayerMoveListener(ApocalypseEvent plugin) {this.plugin = plugin;}

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Check if the plugin is enabled
        if(!plugin.enabled)
            return;

        // Get locations
        Location from = event.getFrom();
        Location to = event.getTo();

        // Check if it was not a new block
        if(to == null || (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()))
            return;

        // Check if they entered a new biome
        Biome fromBiome = from.getBlock().getBiome();
        Biome toBiome = to.getBlock().getBiome();
        if(fromBiome != toBiome)
        {
            // Get the player
            Player player = event.getPlayer();

            // Check if they entered a cold biome
            if(plugin.freezingBiomes.contains(toBiome))
            {
                // Apply freeze ticks
                player.setFreezeTicks(3800);

                //Schedule a freeze refresh
                scheduleFreezingTicks(player);
            }
            // Check if they have freeze ticks and are not in powder snow
            else if(player.isFrozen() && player.getLocation().getBlock().getType() != Material.POWDER_SNOW)
            {
                // Lower freeze ticks
                player.setFreezeTicks(100);
            }
        }
    }

    // Function to schedule reapplying freezing ticks
    void scheduleFreezingTicks(Player player)
    {
        BukkitTask taskID = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if(player.isValid() && !player.isDead() && plugin.freezingBiomes.contains(player.getLocation().getBlock().getBiome())){
                player.setFreezeTicks(3800);
                scheduleFreezingTicks(player);
            }
        }, 180 * 20L); // The 20 converts seconds to ticks
    }
}
