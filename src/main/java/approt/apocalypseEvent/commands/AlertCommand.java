package approt.apocalypseEvent.commands;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class AlertCommand implements CommandExecutor {

    // Connect to the main class
    private final ApocalypseEvent plugin;
    public AlertCommand(ApocalypseEvent plugin) {this.plugin = plugin;}

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args){
        // Checks if they supplied any arguments
        if(args.length < 1) {
            sender.sendMessage("§6[ApocalypseEvent] §cYou must supply a message!§r");
            return false;
        }

        // Checks if they have permissions
        if(sender.hasPermission("apocalypseevent.alert"))
        {
            // Make formatted name
            String name = sender.getName();
            if(sender instanceof Player) {
                // Get the player's team
                Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
                Team team = scoreboard.getEntryTeam(sender.getName());

                if (team != null)
                    name = team.getColor() + team.getPrefix() + team.getColor() + sender.getName() + team.getSuffix() + ChatColor.RESET;
            }

            // Build the message
            StringBuilder builder = new StringBuilder();
            for (String arg : args) builder.append(arg).append(" ");
            String message = builder.toString();

            // Send the message and title to all players on an admin team
            for(Team team : plugin.adminTeams)
            {
                for(String string: team.getEntries())
                {
                    Player teamPlayer = Bukkit.getPlayer(string);
                    if(teamPlayer != null)
                    {
                        // Send the message
                        teamPlayer.sendMessage("§c[Alert] §r<" + name + "§r> " + message);

                        // Send the title
                        String title = "title " + teamPlayer.getName() + " title " + "{text:\"-Alert!-\",color:dark_red}";
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), title);
                        String subtitle = "title " + teamPlayer.getName() + " subtitle {text:\"" + sender.getName() + " has sent an alert!\",color:red}";
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), subtitle);

                        // Play an alert sound
                        teamPlayer.playSound(teamPlayer, Sound.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.AMBIENT, 1, 1);
                    }
                }
            }
        }

        return true;
    }
}
