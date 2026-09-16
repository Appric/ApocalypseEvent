package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Collection;
import java.util.Objects;

public class PlayerDeathListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;

    public PlayerDeathListener(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        // Check if the plugin is enabled
        if (!plugin.enabled)
            return;

        // Call remove repeating tasks function
        plugin.removeRepeatingTasks(event.getEntity());

        // Check to limit range of death messages
        if(plugin.limitDeathMessages)
        {
            // Set player variables
            Player player = event.getEntity();
            Player killer = player.getKiller();

            // Color the message
            String message = event.getDeathMessage();
            event.setDeathMessage(null);
            if (message == null)
                message = "A player has died";
            if(killer != null && killer.getType() == EntityType.PLAYER)
                message = "§4" + message;
            else
                message = "§c" + message;

            // Clear the normal death message
            event.setDeathMessage(null);

            // Initialize location variables and get nearby entities
            Location location = event.getEntity().getLocation();
            int range = Math.max(plugin.deathMessageRange, 0);
            Collection<Entity> nearbyEntities = Objects.requireNonNull(location.getWorld()).getNearbyEntities(location, range, range, range);

            // Send players within range the death message and sound
            Player receiver;
            for(Entity entity : nearbyEntities)
            {
                if(entity instanceof Player) {
                    receiver = (Player) entity;
                    receiver.sendMessage(message);
                    receiver.playSound(receiver, Sound.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE, SoundCategory.AMBIENT, 1, 1);
                }
            }
        }
    }
}
