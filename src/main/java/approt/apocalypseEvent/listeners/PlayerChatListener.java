package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.Objects;

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
            // Declare the player and get team info
            Player player = event.getPlayer();
            Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
            Team team = scoreboard.getEntryTeam(player.getName());

            // Cancel the event
            event.setCancelled(true);

            // Make formatted name
            String name;
            if(team != null)
                name = team.getPrefix() + team.getColor() + player.getDisplayName() + team.getSuffix() + ChatColor.RESET;
            else
                name = player.getDisplayName();

            // Make the message
            String message = String.format(event.getFormat(), name, event.getMessage());

            // Run task in the main thread
            Bukkit.getScheduler().runTask(plugin, () ->{
                // Find nearby entities
                Location location = player.getLocation();
                int range = Math.max(plugin.chatMessageRange, 0);
                Collection<Entity> nearbyEntities = Objects.requireNonNull(location.getWorld()).getNearbyEntities(location, range, range, range);

                // Send the message manually and only to players in range
                for(Entity entity : nearbyEntities)
                    if(entity instanceof Player)
                        entity.sendMessage(message);
            });
        }
    }
}
