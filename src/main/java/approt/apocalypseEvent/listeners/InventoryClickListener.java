package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;

    public InventoryClickListener(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        // Check if the plugin is enabled
        if (!plugin.enabled)
            return;

        if(plugin.limitItems)
        {
            if(event.getCurrentItem() != null) {
                Material material = event.getCurrentItem().getType();
                if(plugin.limitedItems.contains(material) || material.name().startsWith("DIAMOND_") || material.name().startsWith("NETHERITE_"))
                {
                    event.setCurrentItem(null);
                }
            }
        }
    }
}
