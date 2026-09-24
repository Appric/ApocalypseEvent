package approt.apocalypseEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
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

        // Set the player's score
        objective.getScore(player.getName()).setScore(plugin.infectTime);

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

        // Unset the persistent infected data
        player.getPersistentDataContainer().set(new NamespacedKey(plugin, "infected"), PersistentDataType.BOOLEAN, false);
    }
}
