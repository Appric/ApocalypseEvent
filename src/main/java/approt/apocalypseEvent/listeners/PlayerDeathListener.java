package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import approt.apocalypseEvent.InfectionFunctions;
import org.bukkit.*;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.Objects;

public class PlayerDeathListener implements Listener {

    // Connect to the main and InfectedFunctions class
    private final ApocalypseEvent plugin;
    private final InfectionFunctions infectionFunctions;

    public PlayerDeathListener(ApocalypseEvent plugin, InfectionFunctions infectionFunctions) {
        this.plugin = plugin;
        this.infectionFunctions = infectionFunctions;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        // Check if the plugin is enabled
        if (!plugin.enabled)
            return;

        // Call remove repeating tasks function
        plugin.removeRepeatingTasks(event.getEntity());

        // Set player variables
        Player player = event.getEntity();
        Player killer = player.getKiller();

        // Set the damage type
        DamageType damage = event.getDamageSource().getDamageType();

        // Save hunger if it should be saved
        if(plugin.keepHunger)
        {
            plugin.lastHunger.put(player.getUniqueId(), Math.max(player.getFoodLevel(), 8));
        }

        // Check if they should lose their armor
        if(plugin.dropArmorToFire)
        {
            // Check if it was a fire death
            if(plugin.fireDamageTypes.contains(damage) || player.getFireTicks() > 0)
            {
                // Get the armor and add it to drops
                for(ItemStack item : player.getInventory().getArmorContents())
                {
                    event.getDrops().add(item);
                }

                // Clear their armor
                Objects.requireNonNull(player.getEquipment()).setArmorContents(null);
            }
        }

        // Causes the player to drop certain items on death
        for(int slot = 0; slot < player.getInventory().getSize(); slot++)
        {
            ItemStack item = player.getInventory().getItem(slot);
            if(item != null) {
                ItemMeta meta = item.getItemMeta();
                if(meta != null) {
                    if (plugin.dropItems.contains(item.getType()) || (plugin.dropFood && item.getType().isEdible()) || Objects.equals(meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "surge-spear"), PersistentDataType.BOOLEAN), true) || Objects.equals(meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "remedy-item"), PersistentDataType.BOOLEAN), true) || Objects.equals(meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "graupel"), PersistentDataType.BOOLEAN), true)) {
                        event.getDrops().add(item);
                        player.getInventory().setItem(slot, null);
                    }
                }
            }
        }

        // Check to limit range of death messages
        if(plugin.limitDeathMessages)
        {
            // Color the message
            String message = event.getDeathMessage();
            if (message == null)
                message = "A player has died";
            if(killer != null && killer.getType() == EntityType.PLAYER)
                message = "§4" + message;
            else
                message = "§c" + message;

            // Clear the normal death message
            event.setDeathMessage(null);

            // Check if it should be a broadcast
            boolean broadcast = false;
            if(plugin.broadcastMessages)
                for(Location location : plugin.broadcastCords)
                    if(player.getLocation().distanceSquared(location) <= Math.pow(plugin.broadcastCenterRange, 2))
                        broadcast = true;

            if(!broadcast) {
                // Initialize location variables and get nearby entities
                Location location = event.getEntity().getLocation();
                int range = Math.max(plugin.deathMessageRange, 0);
                Collection<Entity> nearbyEntities = Objects.requireNonNull(location.getWorld()).getNearbyEntities(location, range, range, range);

                // Send players within range the death message and sound
                Player receiver;
                for (Entity entity : nearbyEntities) {
                    if (entity instanceof Player) {
                        receiver = (Player) entity;
                        receiver.sendMessage(message);
                        receiver.playSound(receiver, Sound.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE, SoundCategory.AMBIENT, 1, 1);
                    }
                }

                // Send the message to players on an admin team and not in range
                for (Team temp : plugin.adminTeams) {
                    for (String string : temp.getEntries()) {
                        Player teamPlayer = Bukkit.getPlayer(string);
                        if (teamPlayer != null && !nearbyEntities.contains(teamPlayer))
                            teamPlayer.sendMessage("§6--§r" + message);
                    }
                }
            }
            else
            {
                // Make the broadcast
                message = "§7[§eBroadcast§7]§r " + message;
                Bukkit.broadcastMessage(message);
            }
        }

        // Checks if the player is not infected should become infected
        if(Objects.equals(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "infected"), PersistentDataType.BOOLEAN), false) && (damage == DamageType.WITHER || event.getDamageSource().getCausingEntity() instanceof Husk || (killer != null && Objects.equals(killer.getPersistentDataContainer().get(new NamespacedKey(plugin, "infected"), PersistentDataType.BOOLEAN), true))))
        {
            // Get scoreboard details
            Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
            Objective objective = scoreboard.getObjective("infectCount");

            // Starts the infection and adds their new respawn location if they don't already have infection time
            if(objective != null && !objective.getScore(player.getName()).isScoreSet()) {
                infectionFunctions.StartInfection(player);
                plugin.newRespawn.put(player.getUniqueId(), player.getLocation());
            }

            objective = scoreboard.getObjective("infectedPlayers");

            // Creates the objective if it does not exist
            if(objective == null)
            {
                objective = scoreboard.registerNewObjective("infectedPlayers", Criteria.DUMMY, "Gold");
            }

            // Sets the player's score
            int count = objective.getScore(player.getName()).getScore();
            objective.getScore(player.getName()).setScore(count + 1);
        }
    }
}
