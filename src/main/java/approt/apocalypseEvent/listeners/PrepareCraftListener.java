package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;

public class PrepareCraftListener implements Listener {
    // Connect to the main class
    private final ApocalypseEvent plugin;

    public PrepareCraftListener(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event)
    {
        // Check if the plugin is enabled
        if (!plugin.enabled)
            return;

        // Check if the unbreaking craft is enabled
        if(plugin.unbreakingArmorCraft)
        {
            // Get the result and armor meta
            ItemStack result = event.getInventory().getResult();
            if(result != null && result.getType() != Material.AIR)
            {
                // If it is armor, add unbreaking and remove enchant glint
                if(result.getItemMeta() instanceof ArmorMeta meta) {
                    meta.addEnchant(Enchantment.UNBREAKING, 1, false);
                    meta.setEnchantmentGlintOverride(false);
                    result.setItemMeta(meta);
                    event.getInventory().setResult(result);
                }
            }
        }
    }
}
