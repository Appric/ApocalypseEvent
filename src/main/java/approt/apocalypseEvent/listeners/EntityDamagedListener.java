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
import org.bukkit.persistence.PersistentDataType;

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

        // Checks if the entity is a player
        Entity entity = event.getEntity();
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
                        // Checks if it was fire damage
                        if(!(plugin.fireDamageTypes.contains(event.getDamageSource().getDamageType()) || player.getFireTicks() > 0)) {
                            event.setDamage(player.getHealth() - 1);
                        }
                    }

                }
            }

            // Check for channeling on armor to reduce lightning damage
            if(event.getDamageSource().getDamageType() == DamageType.LIGHTNING_BOLT)
            {
                int levels = 0;
                for(ItemStack item : player.getInventory().getArmorContents())
                    if(item != null && item.getType() != Material.AIR)
                        levels += item.getEnchantmentLevel(Enchantment.CHANNELING);

                double newDamage = event.getDamage();
                newDamage = newDamage * (1.0 - ((Math.min(8 * levels, 80) / 100.0)));
                event.setDamage(newDamage);
            }
        }
    }
}
