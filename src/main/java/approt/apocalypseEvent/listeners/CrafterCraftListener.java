package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.inventory.ItemStack;

public class CrafterCraftListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;

    public CrafterCraftListener(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCrafterCraft(CrafterCraftEvent event)
    {
        // Check if the plugin is enabled
        if (!plugin.enabled)
            return;

        if(plugin.limitItems)
        {
            Material material = event.getResult().getType();
            if(plugin.limitedItems.contains(material) || material.name().startsWith("DIAMOND_") || material.name().startsWith("NETHERITE_"))
            {
                event.setResult(new ItemStack(Material.AIR));
            }
        }
    }
}
