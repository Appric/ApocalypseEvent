package approt.apocalypseEvent;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class ApocalypseEventCommand implements CommandExecutor {

    // Connect to the main and InfectionFunctions classes
    private final ApocalypseEvent plugin;
    private final InfectionFunctions infectionFunctions;
    public ApocalypseEventCommand(ApocalypseEvent plugin, InfectionFunctions infectionFunctions) {this.plugin = plugin; this.infectionFunctions = infectionFunctions;}

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args){
        // Checks if they supplied any arguments
        if(args.length < 1 || args.length > 2)
            return false;

        // Checks for the reload command and proper permissions
        if(args[0].equals("reload") && sender.hasPermission("apocalypseevent.reload"))
        {
            // Load the new config values
            plugin.loadConfigValues();

            // Unregister then register the recipes based on the new config values
            plugin.unregisterRecipes();
            plugin.registerRecipes();

            // Send the reloaded message
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

                // Register the recipes
                plugin.registerRecipes();

                // Send enabled message
                sender.sendMessage("§6[ApocalypseEvent] §aThe plugin has been enabled!§r");
            }
            else {
                // Unregister the recipes
                plugin.unregisterRecipes();

                // Send disabled message
                sender.sendMessage("§6[ApocalypseEvent] §cThe plugin has been disabled!§r");
            }
        }
        // Checks for the infect command and proper permissions
        else if(args[0].equals("infect") && sender.hasPermission("apocalypseevent.infect"))
        {
            // Check for another argument
            if(args.length < 2) {
                sender.sendMessage("§6[ApocalypseEvent] §cYou must supply a player!§r");
                return true;
            }

            // Check if the player exists and is online
            Player player = Bukkit.getPlayerExact(args[1]);
            if(player == null)
            {
                sender.sendMessage("§6[ApocalypseEvent] §cThis player does not exist or is not online!§r");
                return true;
            }

            // Call the infection function
            infectionFunctions.StartInfection(player);
        }
        // Checks for the cure command and proper permissions
        else if(args[0].equals("cure") && sender.hasPermission("apocalypseevent.cure"))
        {
            // Check for another argument
            if(args.length < 2) {
                sender.sendMessage("§6[ApocalypseEvent] §cYou must supply a player!§r");
                return true;
            }

            // Check if the player exists and is online
            Player player = Bukkit.getPlayerExact(args[1]);
            if(player == null)
            {
                sender.sendMessage("§6[ApocalypseEvent] §cThis player does not exist or is not online!§r");
                return true;
            }

            // Call cure function
            infectionFunctions.CureInfected(player);
        }
        // Checks for the convert command and proper permissions
        else if(args[0].equals("convert") && sender.hasPermission("apocalypseevent.convert"))
        {
            // Check for another argument
            if(args.length < 2) {
                sender.sendMessage("§6[ApocalypseEvent] §cYou must supply a player!§r");
                return true;
            }

            // Check if the player exists and is online
            Player player = Bukkit.getPlayerExact(args[1]);
            if(player == null)
            {
                sender.sendMessage("§6[ApocalypseEvent] §cThis player does not exist or is not online!§r");
                return true;
            }

            // Checks if the player is already infected
            if(Objects.equals(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "infected"), PersistentDataType.BOOLEAN), true))
            {
                sender.sendMessage("§6[ApocalypseEvent] §cThis player is already infected!§r");
                return true;
            }

            // Call convert function
            infectionFunctions.ConvertToInfected(player);
        }

        return true;
    }
}
