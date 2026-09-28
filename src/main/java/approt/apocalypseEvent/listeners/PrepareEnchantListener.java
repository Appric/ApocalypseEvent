package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;

public class PrepareEnchantListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;

    public PrepareEnchantListener(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareEnchant(PrepareItemEnchantEvent event)
    {
        // Check if the plugin is enabled
        if (!plugin.enabled)
            return;

        // Checks if a player is trying to enchant armor
        ItemStack item = event.getItem();
        if(item.getItemMeta() instanceof ArmorMeta)
            event.setCancelled(true);

        // Note: A player could enchant a book then apply it to armor using an anvil. For now, I am considering this an okay/acceptable loophole
    }
}
