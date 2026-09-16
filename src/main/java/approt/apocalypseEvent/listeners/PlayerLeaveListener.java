package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;

    public PlayerLeaveListener(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        // Check if the plugin is enabled
        if (!plugin.enabled)
            return;

        // Call remove repeating tasks function
        plugin.removeRepeatingTasks(event.getPlayer());
    }
}
