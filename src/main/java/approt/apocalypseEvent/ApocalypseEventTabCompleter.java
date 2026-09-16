package approt.apocalypseEvent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ApocalypseEventTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args)
    {
        List<String> suggestions = new ArrayList<>();

        if(args.length == 1)
        {
            List<String> subcommands = Arrays.asList("reload", "toggle");
            List<String> availableSubCommands = new ArrayList<>();

            // Only auto completes for sub commands you have permission for
            for(String string : subcommands)
            {
                if(sender.hasPermission("apocalypseevent." + string))
                    availableSubCommands.add(string);
            }

            StringUtil.copyPartialMatches(args[0], availableSubCommands, suggestions);

            Collections.sort(suggestions);
        }

        return suggestions;
    }
}
