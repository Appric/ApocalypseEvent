package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

public class PlayerJoinListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;

    public PlayerJoinListener(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        // Declare player
        Player player = event.getPlayer();

        // Checks if the player has received the infected persistent data container key
        if(!player.getPersistentDataContainer().has(new NamespacedKey(plugin, "infected"), PersistentDataType.BOOLEAN))
        {
            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "infected"), PersistentDataType.BOOLEAN, false);
        }

        // Check if the plugin is enabled
        if (!plugin.enabled)
            return;

        // Reinstate repeating tasks in 1 tick
        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.reinstateRepeatingTasks(player), 1L);
    }
}
