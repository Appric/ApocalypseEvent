package approt.apocalypseEvent;

import approt.apocalypseEvent.listeners.BlockPlaceListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class ApocalypseEvent extends JavaPlugin {

    // Public variables
    public Boolean enabled;
    public Boolean limitWater;
    public List<Material> undriedBlocks = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getConsoleSender().sendMessage("[ApocalypseEvent] " + ChatColor.GOLD + "ApocalypseEvent has been " + ChatColor.GREEN + "enabled" + ChatColor.GOLD + "!");

        // Save and load config
        saveDefaultConfig();
        loadConfigValues();

        // Register listeners
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
    }

    // Loads the config values
    public void loadConfigValues(){
        // Reload config
        super.reloadConfig();

        // Set public Booleans
        enabled = getConfig().getBoolean("enabled", true);
        limitWater = getConfig().getBoolean("limit-water", true);

        // Converts material names to materials and puts them in the undriedBlocks list
        undriedBlocks.clear();
        for (String name : getConfig().getStringList("undried-blocks")) {
            Material material = Material.matchMaterial(name);
            if(material != null && material.isBlock())
                undriedBlocks.add(material);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getConsoleSender().sendMessage("[ApocalypseEvent] " + ChatColor.GOLD + "ApocalypseEvent has been " + ChatColor.RED + "disabled" + ChatColor.GOLD + "!");
    }
}
