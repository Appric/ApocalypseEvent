package approt.apocalypseEvent;

import approt.apocalypseEvent.listeners.BlockBreakListener;
import approt.apocalypseEvent.listeners.BlockPlaceListener;
import approt.apocalypseEvent.listeners.BucketEmptyListener;
import approt.apocalypseEvent.listeners.PlayerMoveListener;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ApocalypseEvent extends JavaPlugin {

    // Public variables
    public Boolean enabled;
    public Boolean limitWater;
    public List<Material> undriedBlocks = new ArrayList<>();
    public List<Material> fragileBlocks = new ArrayList<>();
    public List<Biome> freezingBiomes = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getConsoleSender().sendMessage("[ApocalypseEvent] " + ChatColor.GOLD + "ApocalypseEvent has been " + ChatColor.GREEN + "enabled" + ChatColor.GOLD + "!");

        // Save and load config
        saveDefaultConfig();
        loadConfigValues();

        // Register the command
        Objects.requireNonNull(getCommand("apocalypseevent")).setExecutor(new ApocalypseEventCommand(this));

        // Register the tab completer
        Objects.requireNonNull(getCommand("apocalypseevent")).setTabCompleter(new ApocalypseEventTabCompleter());

        // Register listeners
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new BucketEmptyListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
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

        // Converts material names to materials and puts them in the fragileBlocks list
        fragileBlocks.clear();
        for (String name : getConfig().getStringList("fragile-blocks")) {
            Material material = Material.matchMaterial(name);
            if(material != null && material.isBlock())
                fragileBlocks.add(material);
        }

        // Converts biome names to biomes and puts them in the freezingBiomes list
        freezingBiomes.clear();
        for (String name : getConfig().getStringList("freezing-biomes")) {
            Biome biome = Registry.BIOME.get(NamespacedKey.fromString(name));
            if(biome != null)
                freezingBiomes.add(biome);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getConsoleSender().sendMessage("[ApocalypseEvent] " + ChatColor.GOLD + "ApocalypseEvent has been " + ChatColor.RED + "disabled" + ChatColor.GOLD + "!");
    }
}
