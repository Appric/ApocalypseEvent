package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.lang.reflect.Field;
import java.util.List;

public class CommandPreprocessListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;

    public CommandPreprocessListener(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) throws NoSuchFieldException, IllegalAccessException {
        // Check if the plugin is enabled
        if (!plugin.enabled || !plugin.limitChatMessages)
            return;

        // Make the command map accessible
        Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        field.setAccessible(true);
        CommandMap map = (CommandMap) field.get(Bukkit.getServer());

        // Get the command message
        String message = event.getMessage();

        // Get the names of possible commands/aliases from msg
        Command command = map.getCommand("msg");
        if(command != null)
        {
            List<String> commandNames = command.getAliases();
            commandNames.add(command.getName());

            // Checks if the message starts with any of those names
            boolean match = false;
            String commandStart;
            for(String string : commandNames)
            {
                if(message.startsWith("/" + string))
                {
                    match = true;
                    commandStart = "/" + string + " ";
                    break;
                }
            }

            // If found, check if the player is in range
            if(match)
            {
                // Get the players
                Player sender = event.getPlayer();
                Player target;
                if(message.split(message).length > 1)
                    target = Bukkit.getPlayer(message.split(message)[1]);
                else
                    return;

                // Checks if the player exists and is online
                if(target == null || !Bukkit.getOnlinePlayers().contains(target))
                    return;

                // Checks if the player is outside the range
                if(sender.getLocation().distanceSquared(target.getLocation()) > Math.pow(plugin.chatMessageRange, 2))
                {
                    event.setCancelled(true);
                    sender.sendMessage("§6[ApocalypseEvent] §cThis player is out of range!§r");
                }
            }
        }
    }
}
