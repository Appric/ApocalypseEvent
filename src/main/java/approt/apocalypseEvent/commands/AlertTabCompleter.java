package approt.apocalypseEvent.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AlertTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args)
    {
        return new ArrayList<>();
    }
}
