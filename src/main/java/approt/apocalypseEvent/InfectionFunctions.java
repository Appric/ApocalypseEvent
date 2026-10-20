package approt.apocalypseEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Objects;
import java.util.UUID;

public class InfectionFunctions {

    // Connect to the main class
    private final ApocalypseEvent plugin;

    public InfectionFunctions(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    public void StartInfection(Player player)
    {
        // Get scoreboard details
        Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
        Objective objective = scoreboard.getObjective("infectCount");

        // Check if the objective exists
        if(objective == null)
            objective = scoreboard.registerNewObjective("infectCount", Criteria.DUMMY, "Infection Imminent");

        // Check for gold trimmed armor
        int additionalTime = 0;
        ArmorMeta meta;
        for(ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null && item.getItemMeta() instanceof ArmorMeta) {
                meta = (ArmorMeta) item.getItemMeta();
                if (meta != null && meta.hasTrim() && Objects.requireNonNull(meta.getTrim()).getMaterial() == TrimMaterial.GOLD)
                    additionalTime += 30;
            }
        }

        // Set the player's score
        objective.getScore(player.getName()).setScore(plugin.infectTime + additionalTime);

        // Start the repeating task
        plugin.applyInfectionTime(player);
    }

    public void ConvertToInfected(Player player)
    {
        Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
        Team team = scoreboard.getTeam("Infected");

        // Checks if the team exists
        if(team == null) {
            team = scoreboard.registerNewTeam("Infected");
            team.setColor(ChatColor.GRAY);
            team.setPrefix("§8[Infected] ");
        }

        team.addEntry(player.getName());
        player.getPersistentDataContainer().set(new NamespacedKey(plugin, "infected"), PersistentDataType.BOOLEAN, true);

        // Add the effects
        ApplyInfectedEffects(player);

        // If enabled reset their waypoint receive range
        if(plugin.limitLocatorBar)
        {
            AttributeInstance attribute = player.getAttribute(Attribute.WAYPOINT_RECEIVE_RANGE);
            if (attribute != null)
                attribute.setBaseValue(60000000); // The default waypoint receive range for a player is 60,000,0000
        }

        // Check and set how much maximum health infected players lose
        if(plugin.lessInfectedHealth > 0)
        {
            AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
            if (attribute != null)
                attribute.setBaseValue(attribute.getDefaultValue() - plugin.lessInfectedHealth);
        }
    }

    public void CureInfected(Player player)
    {
        UUID uuid = player.getUniqueId();
        // First cancel any active tasks
        if(plugin.infectTasks.containsKey(uuid))
            plugin.infectTasks.get(uuid).cancel();
        plugin.infectTasks.remove(uuid);

        // Get scoreboard details
        Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
        Objective objective = scoreboard.getObjective("infectCount");

        // Check if the objective exists
        if(objective != null) {
            // Attempt to clear the score
            String scoreReset = "scoreboard players reset " + player.getName() + " " + objective.getName();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), scoreReset);
        }

        if(Objects.equals(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "infected"), PersistentDataType.BOOLEAN), true)) {
            // Checks if the survivor team exists
            Team team = scoreboard.getTeam("Survivor");
            if (team == null) {
                team = scoreboard.registerNewTeam("Survivor");
                team.setColor(ChatColor.GRAY);
                team.setPrefix("§e[Survivor] ");
            }

            team.addEntry(player.getName());
        }

        // Sets the waypoint receive range back to 0 if enabled
        if(plugin.limitLocatorBar)
        {
            AttributeInstance attribute = player.getAttribute(Attribute.WAYPOINT_RECEIVE_RANGE);
            if(attribute != null)
                attribute.setBaseValue(0);
        }

        // Unset the persistent infected data
        player.getPersistentDataContainer().set(new NamespacedKey(plugin, "infected"), PersistentDataType.BOOLEAN, false);

        // Check and reset the maximum health of the infected player
        if(plugin.lessInfectedHealth > 0)
        {
            AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
            if (attribute != null)
                attribute.setBaseValue(attribute.getDefaultValue());
        }

        // Clear infected effects
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        player.removePotionEffect(PotionEffectType.HUNGER);
    }

    public void ApplyInfectedEffects(Player player)
    {
        // Apply Main Effects
        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, PotionEffect.INFINITE_DURATION, 0, true));

        // Call Slowness Apply
        ApplyInfectedSlowness(player);
    }

    public void ApplyInfectedSlowness(Player player)
    {
        // Check if the player already has slowness
        if(player.getPotionEffect(PotionEffectType.SLOWNESS) != null)
        {
            player.removePotionEffect(PotionEffectType.SLOWNESS);
        }

        // Get the level of slowness and apply it
        int level = (int) Math.ceil((Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue() - player.getHealth()) / 4);

        // Prevent the slowness from becoming unwalkable
        level = Math.min(level, 5);
        // Checks if the player has raid omen and slightly reduces the slowness if they do
        if(player.hasPotionEffect(PotionEffectType.RAID_OMEN))
            level--;

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, PotionEffect.INFINITE_DURATION, level, true));
    }
}
