package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

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

        // Checks if the locator bar should be range should be hidden for non-infected players
        if(plugin.limitLocatorBar)
        {
            if (Objects.equals(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "infected"), PersistentDataType.BOOLEAN), false))
            {
                // Sets non-infected player's waypoint receive range to be 0
                AttributeInstance attribute = player.getAttribute(Attribute.WAYPOINT_RECEIVE_RANGE);
                if(attribute != null)
                    attribute.setBaseValue(0);
            }
        }

        // Reinstate repeating tasks in 1 tick
        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.reinstateRepeatingTasks(player), 1L);
    }
}
