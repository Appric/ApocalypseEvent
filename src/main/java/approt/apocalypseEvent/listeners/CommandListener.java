package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class CommandListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;

    public CommandListener(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommand(PlayerCommandSendEvent event)
    {
        // Check if the plugin and limitedChatMessages are disabled
        if (plugin.enabled && plugin.limitChatMessages) {
            return;
        }

        // Remove the whisper auto-suggestions
        event.getCommands().remove("whisper");
        event.getCommands().remove("apocalypseevent:whisper");
    }
}
