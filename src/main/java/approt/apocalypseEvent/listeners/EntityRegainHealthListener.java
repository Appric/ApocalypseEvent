package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import approt.apocalypseEvent.InfectionFunctions;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class EntityRegainHealthListener implements Listener {
    // Connect to the main and InfectedFunctions class
    private final ApocalypseEvent plugin;
    private final InfectionFunctions infectionFunctions;

    public EntityRegainHealthListener(ApocalypseEvent plugin, InfectionFunctions infectionFunctions) {
        this.plugin = plugin;
        this.infectionFunctions = infectionFunctions;
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event)
    {
        // Check if the plugin is enabled
        if (!plugin.enabled)
            return;

        // Checks if the entity is a player
        Entity entity = event.getEntity();
        if(entity instanceof Player player) {
            // If the player is infected apply the new slowness effect level
            if(Objects.equals(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "infected"), PersistentDataType.BOOLEAN), true)) {
                // Runs the task later so that the new health (after healing) will be applied
                Bukkit.getScheduler().runTaskLater(plugin, () -> infectionFunctions.ApplyInfectedEffects(player), 1L);
            }
        }
    }
}
