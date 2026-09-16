package approt.apocalypseEvent;

import approt.apocalypseEvent.listeners.*;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(this), this);
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

    // Function to schedule the repeating freeze tick task
    public void applyPlayerFreeze(Player player)
    {
        // Schedule a repeating task to add freezing ticks
        new BukkitRunnable() {
            @Override
            public void run() {
                // Checks if the player is online, exists, is alive, and is still in a cold biome
                if (player.isOnline() && player.isValid() && !player.isDead() && freezingBiomes.contains(player.getLocation().getBlock().getBiome())) {
                    // Apply freeze ticks at twice the speed of powder snow, unless overly exceeding max (accounting for the normal tick decay)
                    player.setFreezeTicks(Math.min(player.getFreezeTicks() + 4, player.getMaxFreezeTicks() + 2));
                }
                else // Cancels the event when it is no longer valid
                    this.cancel();
            }
        }.runTaskTimer(this, 0L, 1L);// The 0 is when to start and the 1 is how many ticks later to run again
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getConsoleSender().sendMessage("[ApocalypseEvent] " + ChatColor.GOLD + "ApocalypseEvent has been " + ChatColor.RED + "disabled" + ChatColor.GOLD + "!");
    }
}
