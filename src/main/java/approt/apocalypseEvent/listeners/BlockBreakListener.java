package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;
    public BlockBreakListener(ApocalypseEvent plugin) {this.plugin = plugin;}

    @EventHandler
    public void onBlockBroken(BlockBreakEvent event)
    {
        // Check if the plugin is enabled
        if(!plugin.enabled)
            return;

        // Check if it is a non-dropping block
        if(plugin.fragileBlocks.contains(event.getBlock().getType()))
        {
            event.setDropItems(false);
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD + "This block is too dried out to pick up!"));
        }
    }
}
