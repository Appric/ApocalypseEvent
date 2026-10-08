package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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

        // Checks if enabled
        if(plugin.speedyCakes) {
            Player player = event.getPlayer();
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
                if (event.getClickedBlock().getType() == Material.CAKE && (player.getFoodLevel() < 20 || player.getGameMode() == GameMode.CREATIVE))
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, false));
            }
        }
    }
}
