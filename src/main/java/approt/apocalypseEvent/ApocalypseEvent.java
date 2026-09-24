package approt.apocalypseEvent;

import approt.apocalypseEvent.listeners.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public final class ApocalypseEvent extends JavaPlugin {

    // Public variables
    public int heatThreshold;
    public int deathMessageRange;
    public int chatMessageRange;
    public Boolean enabled;
    public Boolean limitWater;
    public Boolean heatEnabled;
    public Boolean limitDeathMessages;
    public Boolean limitChatMessages;
    public Boolean dropDriedGhasts;
    public Boolean dropArmorToFire;
    public List<Material> undriedBlocks = new ArrayList<>();
    public List<Material> fragileBlocks = new ArrayList<>();
    public List<Biome> freezingBiomes = new ArrayList<>();
    public HashMap<UUID, BukkitTask> freezeTickTasks = new HashMap<>();
    public HashMap<UUID, BukkitTask> applyHeatTasks = new HashMap<>();

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
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDieListener(this), this);
    }

    // Loads the config values
    public void loadConfigValues(){
        // Reload config
        super.reloadConfig();

        // Set public ints
        heatThreshold = getConfig().getInt("heat-threshold", 12);
        deathMessageRange = getConfig().getInt("death-message-range", 20);
        chatMessageRange = getConfig().getInt("chat-message-range", 20);

        // Set public Booleans
        enabled = getConfig().getBoolean("enabled", true);
        limitWater = getConfig().getBoolean("limit-water", true);
        heatEnabled = getConfig().getBoolean("heat-enabled", true);
        limitDeathMessages = getConfig().getBoolean("limit-death-messages", true);
        limitChatMessages = getConfig().getBoolean("limit-chat-messages", true);
        dropDriedGhasts = getConfig().getBoolean("drop-dried-ghasts", true);
        dropArmorToFire = getConfig().getBoolean("drop-armor-to-fire", true);

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

    // Function to reapply repeating tasks when toggled if applicable
    public void reinstateRepeatingTasks(Player player)
    {
        // Check if the player is in a cold biome and needs freezing ticks
        if(freezingBiomes.contains(player.getLocation().getBlock().getBiome()))
            applyPlayerFreeze(player);

        // Apply player heat if enabled
        if(heatEnabled)
            startPlayerHeat(player);
    }

    // Function to remove repeating tasks
    public void removeRepeatingTasks(Player player)
    {
        // Freezing ticks
        freezeTickTasks.remove(player.getUniqueId());

        // Heat
        applyHeatTasks.remove(player.getUniqueId());
    }

    // Function to schedule the repeating freeze tick task
    public void applyPlayerFreeze(Player player)
    {
        // Schedule a repeating task to add freezing ticks
        freezeTickTasks.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                // Checks if the player is online, exists, is alive, and is still in a cold biome
                if(enabled && player.isOnline() && player.isValid() && !player.isDead() && freezingBiomes.contains(player.getLocation().getBlock().getBiome())) {
                    // Apply freeze ticks at twice the speed of powder snow, unless overly exceeding max (accounting for the normal tick decay)
                    player.setFreezeTicks(Math.min(player.getFreezeTicks() + 4, player.getMaxFreezeTicks() + 2));
                }
                else // Cancels the event when it is no longer valid
                {
                    freezeTickTasks.remove(player.getUniqueId());
                    this.cancel();
                }
            }
        }.runTaskTimer(this, 0L, 1L)); // The 0 is when to start and the 1 is how many ticks later to run again
    }

    // Function to apply the heat repeating task
    public void startPlayerHeat(Player player)
    {
        // Schedule a repeating task to handle heat functions
        applyHeatTasks.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                // Initialize variables
                int heat = 0;
                long time = player.getWorld().getTime();
                Location location = player.getLocation();
                int highY = Objects.requireNonNull(location.getWorld()).getHighestBlockYAt(location);

                // Checks if heat is enabled, the player is online, exists, and is alive
                if(enabled && heatEnabled && player.isOnline() && player.isValid() && !player.isDead()) {
                    // Checks to not apply heat in freezing biomes
                    if(!freezingBiomes.contains(location.getBlock().getBiome())) {
                        // First calculates the heat from armor
                        heat += (int) Math.floor(Objects.requireNonNull(player.getAttribute(Attribute.ARMOR)).getValue());

                        // Then add heat based on time of day and if exposed to sunlight
                        if (time > 0 && time < 12000) {

                            // Check if it is close to midday
                            if (time > 4500 && time < 7500) // Heat of the day
                                heat++;

                            // Check if exposed to the sky
                            if (location.getY() >= highY) {
                                heat++;
                                // Add extra heat if the player has no helmet
                                if (player.getInventory().getHelmet() == null) {
                                    heat += 4;
                                    // Even more extra heat if also midday, exposed to the sky, and no helmet
                                    if (time > 4500 && time < 7500)
                                        heat += 4;
                                }
                            }
                        }
                        else // Slightly reduce heat during the night
                        {
                            heat -= 2;
                            // Remove even more heat once the sunsets
                            if (time > 14000 && time < 22000)
                                heat -= 2;
                        }

                        // Add heat depending on how far they are underground (thermal heating)
                        int Y = location.getBlockY();
                        if(Y < 63)
                            heat += (int)Math.ceil(Math.abs(63 - Y) / 25.0);

                        // Remove some heat if under the wind charged effect
                        for(PotionEffect effect : player.getActivePotionEffects())
                            if(effect.getType() == PotionEffectType.WIND_CHARGED)
                                heat -= 4;

                        // Remove some heat if in water
                        if(player.isInWater())
                            heat--;

                        // Check if heat is at the threshold
                        if (heat >= heatThreshold) {
                            player.damage(2, DamageSource.builder(DamageType.DRY_OUT).build());
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD + "You are starting to overheat!"));
                        }
                    }
                }
                else // Cancels the event when it is no longer valid
                {
                    applyHeatTasks.remove(player.getUniqueId());
                    this.cancel();
                }
            }
        }.runTaskTimer(this, 0L, 2 * 20L)); // The 0 is when to start and the * 20 is to convert seconds into ticks
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getConsoleSender().sendMessage("[ApocalypseEvent] " + ChatColor.GOLD + "ApocalypseEvent has been " + ChatColor.RED + "disabled" + ChatColor.GOLD + "!");
    }
}
