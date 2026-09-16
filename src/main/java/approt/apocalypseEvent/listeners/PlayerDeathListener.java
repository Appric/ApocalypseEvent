package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

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
    }
}
