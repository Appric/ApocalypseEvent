package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandPreprocessListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;

    public CommandPreprocessListener(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        // Check if the plugin is enabled
        if (!plugin.enabled || !plugin.limitChatMessages)
            return;

        // Get the players
        Player sender = event.getPlayer();
        Player target;

        // Get the command message
        String message = event.getMessage();

        // Get the command that was run by stripping spaces
        String com = message.split(" ")[0];

        List<String> commandNames = new ArrayList<>(List.of("msg", "tell", "w"));


        // Checks if the message starts with any of those names
        boolean match = false;
        for (String string : commandNames) {
            if (com.startsWith("/" + string) || com.startsWith("/minecraft:" + string)) {
                match = true;
                break;
            }
        }

        // If found, check if the player is in range
        if (match) {
            // Checks if the sender is on an admin team
            Team team = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getEntryTeam(sender.getName());
            if (team != null && plugin.adminTeams.contains(team))
                return;

            if (message.split(message).length > 1)
                target = Bukkit.getPlayer(message.split(message)[1]);
            else
                return;

            // Checks if the player exists and is online
            if (target == null || !Bukkit.getOnlinePlayers().contains(target))
                return;

            // Checks if the player is outside the range (except admin team players)
            team = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getEntryTeam(target.getName());
            if (sender.getLocation().distanceSquared(target.getLocation()) > Math.pow(plugin.chatMessageRange, 2) && (team == null || !plugin.adminTeams.contains(team))) {
                event.setCancelled(true);
                sender.sendMessage("§6[ApocalypseEvent] §cThis player is out of range!§r");
            }
        }
    }
}
