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

        // Checks if they are infected
        boolean infected = Objects.equals(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "infected"), PersistentDataType.BOOLEAN), true);
        if(infected) {
            // Checks if it was an item that might clear the infected effects
            if (material == Material.MILK_BUCKET || material == Material.POTION)
                Bukkit.getScheduler().runTaskLater(plugin, () -> infectionFunctions.ApplyInfectedEffects(player), 1L); // Runs the task after the item consume event so the effects apply after the item clears effects
        }

        // Checks if the item is a remedy item
        if(Objects.equals(Objects.requireNonNull(event.getItem().getItemMeta()).getPersistentDataContainer().get(new NamespacedKey(plugin, "remedy-item"), PersistentDataType.BOOLEAN), true))
        {
            // Checks if they are infected
            if(infected)
                infectionFunctions.CureInfected(player);
            else if(plugin.remedyImmunizes) // If immunization enabled, immunize non-infected players
                infectionFunctions.Immunize(player);
        }
    }
}
