package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;

public class PrepareAnvilListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;

    public PrepareAnvilListener(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event)
    {
        // Get the result and armor meta
        ItemStack result = event.getResult();
        if(result != null && result.getType() != Material.AIR) {
            Material material = result.getType();

            // Check if it is an item that should be limited
            if (plugin.limitItems) {
                if (plugin.limitedItems.contains(material) || material.name().startsWith("DIAMOND_") || material.name().startsWith("NETHERITE_")) {
                    event.setResult(new ItemStack(Material.AIR));
                    return;
                }
            }

            if (result.getItemMeta() instanceof ArmorMeta meta) {
                meta.removeEnchantments();
                result.setItemMeta(meta);
            }
        }
    }
}
