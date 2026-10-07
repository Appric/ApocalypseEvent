package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class CommandPreprocessListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;

    public CommandPreprocessListener(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        // Check if the plugin and limitedChatMessages are disabled
        if (plugin.enabled && plugin.limitChatMessages)
            return;

        // Get the names of possible commands/aliases from whisper
        PluginCommand command = Bukkit.getPluginCommand("whisper");
        if(command != null)
        {
            List<String> commandNames = command.getAliases();
            commandNames.add(command.getName());

            // Checks if the message starts with any of those names
            boolean match = false;
            for(String string : commandNames)
            {
                if(event.getMessage().startsWith("/" + string))
                {
                    match = true;
                    break;
                }
            }

            // If found, directly send the message to the game instead
            if(match)
            {
                event.setCancelled(true);
                Bukkit.dispatchCommand(event.getPlayer(), "minecraft:" + event.getMessage().substring(1));
            }
        }
    }
}
