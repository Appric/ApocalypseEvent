package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class PrepareSmithingListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;

    public PrepareSmithingListener(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareSmithing(PrepareSmithingEvent event)
    {
        // Check if the plugin is enabled
        if (!plugin.enabled)
            return;

        // Get the result and armor meta
        ItemStack result = event.getResult();
        if(result != null && result.getType() != Material.AIR) {
            Material material = result.getType();

            // Check if it is an item that should be limited
            if(plugin.limitItems)
            {
                if(plugin.limitedItems.contains(material) || material.name().startsWith("DIAMOND_") || material.name().startsWith("NETHERITE_"))
                {
                    event.getInventory().setResult(new ItemStack(Material.AIR));
                    return;
                }
            }

            if(result.getItemMeta() instanceof ArmorMeta meta) {
                // Check for and apply enchants
                if (meta.hasTrim()) {
                    ArmorTrim trim = meta.getTrim();
                    if (trim != null) {
                        NamespacedKey key = trim.getMaterial().getKeyOrNull();
                        if (key != null) {
                            // Declare enchant variables
                            Enchantment enchantment;
                            int level = 1;

                            // Removes enchants to prevent stacking enchants by switching trims
                            meta.removeEnchantments();

                            // Check for material type
                            switch (key.getKey()) {
                                case "netherite", "amethyst":
                                    enchantment = Enchantment.MENDING;
                                    break;
                                case "diamond":
                                    enchantment = Enchantment.UNBREAKING;
                                    level = 3;
                                    // Also checks if it is a helmet to additionally apply aqua affinity
                                    if(result.getType().getEquipmentSlot() == EquipmentSlot.HEAD)
                                        meta.addEnchant(Enchantment.AQUA_AFFINITY, 1, false);
                                    break;
                                case "iron":
                                    enchantment = Enchantment.PROTECTION;
                                    level = 2;
                                    break;
                                case "gold":
                                    enchantment = Enchantment.PROJECTILE_PROTECTION;
                                    level = 2;
                                    break;
                                case "copper":
                                    enchantment = Enchantment.CHANNELING;
                                    break;
                                case "emerald":
                                    enchantment = Enchantment.THORNS;
                                    level = 2;
                                    // Also checks if it is not a helmet to additionally apply aqua affinity
                                    if(result.getType().getEquipmentSlot() != EquipmentSlot.HEAD)
                                        meta.addEnchant(Enchantment.AQUA_AFFINITY, 1, false);
                                    break;
                                case "lapis":
                                    enchantment = Enchantment.BLAST_PROTECTION;
                                    level = 2;
                                    break;
                                case "quartz":
                                    enchantment = Enchantment.WIND_BURST;
                                    break;
                                case "resin", "redstone":
                                    enchantment = Enchantment.FIRE_PROTECTION;
                                    break;
                                default:
                                    enchantment = Enchantment.PROTECTION;
                            }

                            // Add back the unbreaking enchant if enabled
                            if(plugin.unbreakingArmorCraft)
                                meta.addEnchant(Enchantment.UNBREAKING, 1, false);

                            // Check if the item is charged
                            if (Objects.equals(meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "charged"), PersistentDataType.BOOLEAN), true)) {
                                meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "charged"), PersistentDataType.BOOLEAN, false);
                                meta.setEnchantmentGlintOverride(false);
                            }

                            // Actually apply the enchantment to the item
                            meta.addEnchant(enchantment, level, false);
                            meta.setEnchantmentGlintOverride(false);
                            result.setItemMeta(meta);
                        }
                    }
                }
            }
        }
    }
}
