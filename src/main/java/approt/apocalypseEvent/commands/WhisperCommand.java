package approt.apocalypseEvent.commands;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class WhisperCommand implements CommandExecutor {

    // Connect to the main class
    private final ApocalypseEvent plugin;

    public WhisperCommand(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        // Checks if they have permission
        if(!sender.hasPermission("apocalypseevent.whisper")) {
            sender.sendMessage("§cYou do not have permission to use that command!§r");
            return true;
        }

        // Checks if they supplied a player
        if(args.length == 0) {
            sender.sendMessage("§6[ApocalypseEvent] §cYou must supply a player!§r");
            return true;
        }

        // Check if the player exists and is online
        Player player = Bukkit.getPlayerExact(args[0]);
        if(player == null)
        {
            sender.sendMessage("§6[ApocalypseEvent] §cThis player does not exist or is not online!§r");
            return true;
        }

        // Check if they supplied a message
        if(args.length == 1) {
            sender.sendMessage("§6[ApocalypseEvent] §cYou must supply a message!§r");
            return true;
        }


        // Checks if the player is in range
        if(sender instanceof Player && player.getLocation().distanceSquared(((Player) sender).getLocation()) > Math.pow(plugin.chatMessageRange,2)) {
            sender.sendMessage("§6[ApocalypseEvent] §cThis player is out of range!§r");
            return true;
        }

        // Create the message
        StringBuilder builder = new StringBuilder();
        for(int loop = 1; loop < args.length; loop++)
            builder.append(args[loop]).append(" ");
        String message = builder.toString();

        // Send the messages
        if(sender instanceof Player)
            player.sendMessage("§7§o" + ((Player) sender).getDisplayName() + " whispers to you: " + message + "§r");
        else
        {
            if(sender instanceof ConsoleCommandSender)
                player.sendMessage("§7§oSever whispers to you: " + message + "§r");
            else
                player.sendMessage("§7§o" + sender.getName() + " whispers to you: " + message + "§r");
        }
        sender.sendMessage("§7§oYou whisper to " + player.getDisplayName() + ": " + message + "§r");

        return true;
    }
}