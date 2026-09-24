package approt.apocalypseEvent;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class ApocalypseEventTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args)
    {
        List<String> suggestions = new ArrayList<>();
        List<String> subcommands;
        List<String> availableSubCommands = new ArrayList<>();

        if(args.length == 1)
        {
            subcommands = Arrays.asList("reload", "toggle", "infect", "cure");

            // Only auto completes for sub commands you have permission for
            for(String string : subcommands)
            {
                if(sender.hasPermission("apocalypseevent." + string))
                    availableSubCommands.add(string);
            }

            StringUtil.copyPartialMatches(args[0], availableSubCommands, suggestions);

            Collections.sort(suggestions);
        }
        else if(args.length == 2)
        {
            // Check if it is for the infect command
            if(args[0].equals("infect") || args[0].equals("cure"))
            {
                // Suggest online players
                for(Player player : Bukkit.getOnlinePlayers())
                {
                    availableSubCommands.add(player.getName());
                }
            }

            StringUtil.copyPartialMatches(args[1], availableSubCommands, suggestions);

            Collections.sort(suggestions);
        }

        return suggestions;
    }
}
