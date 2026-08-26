package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BlockPlaceListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;
    public BlockPlaceListener(ApocalypseEvent plugin) {this.plugin = plugin;}

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        // Check if the plugin is enabled
        if(!plugin.enabled)
            return;

        // Register variables
        Block block = event.getBlock();
        Material material = block.getType();
        List<Block> adjacentBlocks = new ArrayList<>();
        boolean isValid = false;

        // Fill adjacentBlocks
        adjacentBlocks.add(block.getRelative(BlockFace.UP));
        adjacentBlocks.add(block.getRelative(BlockFace.DOWN));
        adjacentBlocks.add(block.getRelative(BlockFace.NORTH));
        adjacentBlocks.add(block.getRelative(BlockFace.EAST));
        adjacentBlocks.add(block.getRelative(BlockFace.SOUTH));
        adjacentBlocks.add(block.getRelative(BlockFace.WEST));

        // Checks if any adjacent blocks are a valid type
        if(plugin.undriedBlocks.contains(material) || (plugin.limitWater && material == Material.WATER))
        {
            for(Block adjacentBlock : adjacentBlocks)
            {
                if(plugin.undriedBlocks.contains(adjacentBlock.getType())) {
                    isValid = true;
                    break;
                }
            }

            // If there are no valid adjacent blocks then cancel the event and send a message
            if(!isValid) {
                event.setCancelled(true);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD + "The environment is too arid to place this here!"));
            }
        }
    }
}
