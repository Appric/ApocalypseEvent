package approt.apocalypseEvent.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WhisperTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        List<String> suggestions = new ArrayList<>();
        List<String> availableNames = new ArrayList<>();

        // Checks for proper amount of arguments
        if(args.length == 1)
        {
            // Suggest online players
            for(Player player : Bukkit.getOnlinePlayers())
            {
                availableNames.add(player.getName());
            }

            StringUtil.copyPartialMatches(args[0], availableNames, suggestions);

            Collections.sort(suggestions);
        }
        return suggestions;
    }
}
