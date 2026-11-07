package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class ItemMoveListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;

    public ItemMoveListener(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemMove(InventoryMoveItemEvent event)
    {
        // Check if the plugin is enabled
        if (!plugin.enabled)
            return;

        if(plugin.limitItems)
        {
            Material material = event.getItem().getType();
            if(plugin.limitedItems.contains(material) || material.name().startsWith("DIAMOND_") || material.name().startsWith("NETHERITE_"))
            {
                event.setItem(null);
            }
        }
    }
}
