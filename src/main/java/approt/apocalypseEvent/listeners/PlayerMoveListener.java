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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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

        // Check if they are in water
        if(player.isInWater())
        {
            // Gets if the player is infected or not
            boolean infected = Objects.equals(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "infected"), PersistentDataType.BOOLEAN), true);

            // Checks if water can treat infected and in the proper biome
            if(plugin.waterCanTreat && (fromBiome == plugin.treatBiome || toBiome == plugin.treatBiome))
            {
                // Don't cure infected players
                if(!infected)
                    infectionFunctions.CureInfected(player);
            }
            else if(plugin.poisonousWater)
            {
                // Potion Effect variable
                PotionEffect effect;

                // Water is more deadly to infected players
                if(infected) {
                    effect = player.getPotionEffect(PotionEffectType.WITHER);
                    // Check if effect is low enough
                    if(effect == null || effect.getDuration() < 40)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 1, true));
                }
                else {
                    effect = player.getPotionEffect(PotionEffectType.POISON);
                    // Check if effect is low enough
                    if(effect == null || effect.getDuration() < 40)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 0, true));
                }
            }
        }
    }
}
