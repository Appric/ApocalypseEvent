package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public class PlayerChatListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;

    public PlayerChatListener(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        // Check if the plugin is enabled
        if (!plugin.enabled)
            return;

        // Check if limited chat messages are enabled
        if(plugin.limitChatMessages)
        {
            // Get the list of recipients
            Set<Player> recipients = event.getRecipients();
            recipients.clear();

            // Run task in the main thread
            Bukkit.getScheduler().runTask(plugin, () ->{
                // Find nearby entities
                Location location = event.getPlayer().getLocation();
                int range = Math.max(plugin.chatMessageRange, 0);
                Collection<Entity> nearbyEntities = Objects.requireNonNull(location.getWorld()).getNearbyEntities(location, range, range, range);

                // Send the message only to players in range
                for(Entity entity : nearbyEntities)
                    if(entity instanceof Player)
                        recipients.add((Player)entity);
            });
        }
    }
}
