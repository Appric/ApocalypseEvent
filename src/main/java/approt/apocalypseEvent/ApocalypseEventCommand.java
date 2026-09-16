package approt.apocalypseEvent;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
        // Checks for the toggle command and proper permissions
        else if(args[0].equals("toggle") && sender.hasPermission("apocalypseevent.toggle"))
        {
            // Check if already enabled or not
            if(plugin.enabled)
                plugin.getConfig().set("enabled", false);

            else
                plugin.getConfig().set("enabled", true);

            // Save updated value
            plugin.saveConfig();
            plugin.loadConfigValues();

            if(plugin.enabled) {
                // Reinstate repeating tasks for all online players
                for(Player player : Bukkit.getOnlinePlayers())
                    plugin.reinstateRepeatingTasks(player);

                // Send enabled message
                sender.sendMessage("§6[ApocalypseEvent] §aThe plugin has been enabled!§r");
            }
            else
                // Send disabled message
                sender.sendMessage("§6[ApocalypseEvent] §cThe plugin has been disabled!§r");
        }

        return true;
    }
}
