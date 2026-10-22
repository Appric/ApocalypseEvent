package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class PlayerInteractListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;
    public PlayerInteractListener(ApocalypseEvent plugin) {this.plugin = plugin;}

    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        // Check if the plugin is enabled
        if(!plugin.enabled)
            return;

        // Get the player
        Player player = event.getPlayer();

        // Checks if cakes are enabled
        if(plugin.speedyCakes) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
                if (event.getClickedBlock().getType() == Material.CAKE && (player.getFoodLevel() < 20 || player.getGameMode() == GameMode.CREATIVE))
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, false));
            }
        }

        // Checks if fire flowers are enabled
        if(plugin.fireFlowers)
        {
            if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
            {
                ItemStack item = event.getItem();
                if(item != null && item.getType() == Material.TORCHFLOWER)
                {
                    ItemMeta meta = item.getItemMeta();
                    if(meta != null) {
                        PersistentDataContainer container = meta.getPersistentDataContainer();
                        NamespacedKey key = new NamespacedKey(plugin, "fire-uses");

                        int uses = Objects.requireNonNullElse(container.get(key, PersistentDataType.INTEGER), plugin.fireFlowerUses);

                        // Execute fireball logic
                        Entity fireball = Objects.requireNonNull(player.getLocation().getWorld()).spawnEntity(player.getEyeLocation(), EntityType.SMALL_FIREBALL);
                        fireball.setVelocity(player.getEyeLocation().getDirection());

                        // Lower uses
                        uses--;
                        container.set(key, PersistentDataType.INTEGER, uses);

                        // If this is the item's last use remove it and reset uses
                        if(uses == 0)
                        {
                            container.set(key, PersistentDataType.INTEGER, plugin.fireFlowerUses);
                            if(item.getAmount() == 1)
                                player.getInventory().setItemInMainHand(null);
                            else
                                item.setAmount(item.getAmount() - 1);
                        }

                        // Set the item meta back to the item
                        item.setItemMeta(meta);
                    }
                }
            }
        }
    }
}
