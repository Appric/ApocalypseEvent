package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import approt.apocalypseEvent.InfectionFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class ItemConsumeListener implements Listener {

    // Connect to the main and InfectedFunctions class
    private final ApocalypseEvent plugin;
    private final InfectionFunctions infectionFunctions;

    public ItemConsumeListener(ApocalypseEvent plugin, InfectionFunctions infectionFunctions) {
        this.plugin = plugin;
        this.infectionFunctions = infectionFunctions;
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event)
    {
        // Check if the plugin is enabled
        if (!plugin.enabled)
            return;

        Player player = event.getPlayer();

        Material material = event.getItem().getType();
        if(material == Material.MILK_BUCKET)
        {
            // Check if they are an infected player
            if(Objects.equals(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "infected"), PersistentDataType.BOOLEAN), true))
            {
                // Runs the task after the item consume event so the effects apply after the milk clears all effects
                Bukkit.getScheduler().runTaskLater(plugin, () -> infectionFunctions.ApplyInfectedEffects(player), 1L);
            }
        }
    }
}
