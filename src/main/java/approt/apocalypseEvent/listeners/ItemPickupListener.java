package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class ItemPickupListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;

    public ItemPickupListener(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event)
    {
        // Check if the plugin is enabled
        if (!plugin.enabled)
            return;

        // Check if it is a limited item
        if(plugin.limitItems)
        {
            Material material = event.getItem().getItemStack().getType();
            if(plugin.limitedItems.contains(material) || material.name().startsWith("DIAMOND_") || material.name().startsWith("NETHERITE_"))
            {
                event.getItem().setItemStack(new ItemStack(Material.AIR));
            }
        }
    }
}
