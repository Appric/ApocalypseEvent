package approt.apocalypseEvent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

public class ApocalypseEventCommand implements CommandExecutor {

    // Connect to the main class
    private final ApocalypseEvent plugin;
    public ApocalypseEventCommand(ApocalypseEvent plugin) {this.plugin = plugin;}

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args){
        // Checks if they supplied any arguments
        if(args.length != 1)
            return false;

        // Checks for the reload command and proper permissions
        if(args[0].equals("reload") && sender.hasPermission("apocalypseevent.reload"))
        {
            plugin.loadConfigValues();
            sender.sendMessage("§6[ApocalypseEvent] §aThe config has been reloaded!§r");
        }
        return true;
    }
}
