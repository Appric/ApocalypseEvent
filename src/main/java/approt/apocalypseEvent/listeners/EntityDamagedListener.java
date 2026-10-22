package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import approt.apocalypseEvent.InfectionFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class EntityDamagedListener implements Listener {

    // Connect to the main and InfectedFunctions class
    private final ApocalypseEvent plugin;
    private final InfectionFunctions infectionFunctions;

    public EntityDamagedListener(ApocalypseEvent plugin, InfectionFunctions infectionFunctions) {
        this.plugin = plugin;
        this.infectionFunctions = infectionFunctions;
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageEvent event)
    {
        // Check if the plugin is enabled
        if (!plugin.enabled)
            return;

        // Gets the entities
        Entity entity = event.getEntity();
        Entity causingEntity = event.getDamageSource().getCausingEntity();

        // Checks if the entity is a player
        if(entity instanceof Player player) {
            // If the player is infected apply the new slowness effect level
            if (Objects.equals(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "infected"), PersistentDataType.BOOLEAN), true)) {
                // Runs the task later so that the new health (after damage) will be applied
                Bukkit.getScheduler().runTaskLater(plugin, () -> infectionFunctions.ApplyInfectedEffects(player), 1L);

                // Checks if only fire can kill them
                if(plugin.fireOnlyDeathInfected)
                {
                    // Checks if it is enough damage to kill
                    if(player.getHealth() - event.getDamage() < 1)
                    {
                        // Checks if it was fire, wither, or freezing damage
                        if(!(plugin.fireDamageTypes.contains(event.getDamageSource().getDamageType()) || player.getFireTicks() > 0 || event.getDamageSource().getDamageType() == DamageType.WITHER || event.getDamageSource().getDamageType() == DamageType.FREEZE)) {
                            event.setDamage(player.getHealth() - 1);
                        }
                    }

                }
            }

            // Check for channeling on armor to reduce lightning damage
            if(event.getDamageSource().getDamageType() == DamageType.LIGHTNING_BOLT || event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING)
            {
                int levels = 0;
                for(ItemStack item : player.getInventory().getArmorContents())
                    if(item != null && item.getType() != Material.AIR) {
                        // Add to the total levels of damage reduction
                        levels += item.getEnchantmentLevel(Enchantment.CHANNELING);

                        // Checks if there is active rain/lightning (so channeling can't charge itself in clear weather)
                        if(player.getWorld().hasStorm() || player.getWorld().isThundering()) {
                            // Set the channeling charged tag and enchant glint
                            ItemMeta meta = item.getItemMeta();
                            if (meta instanceof ArmorMeta) {
                                //Check if the item has channeling
                                if(meta.hasEnchant(Enchantment.CHANNELING)) {
                                    // Set the persistent data
                                    meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "charged"), PersistentDataType.BOOLEAN, true);

                                    // Add enchant glint
                                    meta.setEnchantmentGlintOverride(true);

                                    // Now set the item's actual meta
                                    item.setItemMeta(meta);
                                }
                            }
                        }

                    }

                double newDamage = event.getDamage();
                newDamage = newDamage * (1.0 - ((Math.min(16 * levels, 80) / 100.0)));
                event.setDamage(newDamage);
            }
        }

        if(causingEntity instanceof Player attacker) {
            // Check for an attack with charged armor if clear
            if (entity.getWorld().isClearWeather()) {
                ArmorMeta meta;
                for (ItemStack item : attacker.getInventory().getArmorContents()) {
                    if (item != null && item.getItemMeta() instanceof ArmorMeta) {
                        meta = (ArmorMeta) item.getItemMeta();
                        // Check if it has the charged data
                        if (meta != null && Objects.equals(meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "charged"), PersistentDataType.BOOLEAN), true)) {
                            // Unset the charged data
                            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "charged"), PersistentDataType.BOOLEAN, false);
                            meta.setEnchantmentGlintOverride(false);
                            item.setItemMeta(meta);

                            // Strike lightning at the causing entity
                            Bukkit.getScheduler().runTaskLater(plugin, () -> entity.getWorld().strikeLightning(entity.getLocation()), 1L);

                            // Break the loop to preserve other item pieces charged status
                            break;
                        }
                    }
                }
            }

            // Check if the attacker is infected and should inflict wither
            if(plugin.infectedInflictEffect && Objects.equals(attacker.getPersistentDataContainer().get(new NamespacedKey(plugin, "infected"), PersistentDataType.BOOLEAN), true))
            {
                if(!plugin.inflictHandsOnly || attacker.getInventory().getItemInMainHand().getType() == Material.AIR)
                {
                    if(entity instanceof Player player)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, plugin.inflictDuration * 20, 0));
                }
            }
        }
    }
}
