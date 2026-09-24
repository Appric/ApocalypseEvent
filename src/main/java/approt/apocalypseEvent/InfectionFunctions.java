package approt.apocalypseEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

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
    }
}
