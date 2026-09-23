package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDieListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;
    public EntityDieListener(ApocalypseEvent plugin) {this.plugin = plugin;}

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event)
    {
        // Check if the plugin is enabled
        if(!plugin.enabled)
            return;

        // Check if dried ghast drops is enabled and if the entity dying is a happy ghast
        if(plugin.dropDriedGhasts && event.getEntityType() == EntityType.HAPPY_GHAST)
            event.getDrops().add(new ItemStack(Material.DRIED_GHAST));
    }
}
