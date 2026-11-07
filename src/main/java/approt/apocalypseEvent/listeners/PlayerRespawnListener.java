package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import approt.apocalypseEvent.InfectionFunctions;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.UUID;

public class PlayerRespawnListener implements Listener {

    // Connect to the main and InfectedFunctions class
    private final ApocalypseEvent plugin;
    private final InfectionFunctions infectionFunctions;

    public PlayerRespawnListener(ApocalypseEvent plugin, InfectionFunctions infectionFunctions) {
        this.plugin = plugin;
        this.infectionFunctions = infectionFunctions;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        // Check if the plugin is enabled
        if (!plugin.enabled)
            return;

        // Declare player
        Player player = event.getPlayer();

        // Reinstate repeating tasks in 1 tick (and infected effects if necessary)
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.reinstateRepeatingTasks(player);
            if(Objects.equals(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "infected"), PersistentDataType.BOOLEAN), true))
                infectionFunctions.ApplyInfectedEffects(player);
        }, 1L);

        // Checks if a player has a new respawn point
        UUID uuid = player.getUniqueId();
        if(plugin.phoenixRespawn.containsKey(uuid))
        {
            // Sets the new respawn
            event.setRespawnLocation(plugin.phoenixRespawn.get(uuid));

            // Puts the player into spectator
            Bukkit.getScheduler().runTaskLater(plugin, () -> player.setGameMode(GameMode.SPECTATOR), 1L);

            // Brings the player out of spectator at the respawn location, sets them on fire, and removes the entry from the hash map
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(player.isOnline()) {
                    player.teleport(plugin.phoenixRespawn.get(uuid), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    player.setGameMode(GameMode.SURVIVAL);
                    player.setFireTicks(120);
                    plugin.phoenixRespawn.remove(uuid);
                }
            }, 5 * 20L);
        }
        else if(plugin.newRespawn.containsKey(uuid))
        {
            // Sets the new respawn and removes it from the hash map
            event.setRespawnLocation(plugin.newRespawn.get(uuid));
            plugin.newRespawn.remove(uuid);
        }

        // Checks if a player's hunger needs to be set
        if(plugin.keepHunger)
        {
            if(plugin.lastHunger.containsKey(player.getUniqueId()))
                Bukkit.getScheduler().runTaskLater(plugin, () -> player.setFoodLevel(plugin.lastHunger.get(player.getUniqueId())), 1L);
        }
    }
}
