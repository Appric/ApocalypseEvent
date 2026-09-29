package approt.apocalypseEvent;

import approt.apocalypseEvent.listeners.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public final class ApocalypseEvent extends JavaPlugin {

    // Public variables
    public int heatThreshold;
    public int deathMessageRange;
    public int chatMessageRange;
    public int infectTime;
    public int juiceDuration;
    public int lightningFrequency;
    public Boolean enabled;
    public Boolean limitWater;
    public Boolean heatEnabled;
    public Boolean limitDeathMessages;
    public Boolean limitChatMessages;
    public Boolean dropDriedGhasts;
    public Boolean dropArmorToFire;
    public Boolean infection;
    public Boolean fireOnlyDeathInfected;
    public Boolean customRecipes;
    public Boolean lightningStorms;
    public Boolean randomLightning;
    public Boolean unbreakingArmorCraft;
    public List<Material> undriedBlocks = new ArrayList<>();
    public List<Material> fragileBlocks = new ArrayList<>();
    public List<Biome> freezingBiomes = new ArrayList<>();
    public HashMap<UUID, BukkitTask> freezeTickTasks = new HashMap<>();
    public HashMap<UUID, BukkitTask> applyHeatTasks = new HashMap<>();
    public HashMap<UUID, BukkitTask> infectTasks = new HashMap<>();

    // List of fire related damage types
    public List<DamageType> fireDamageTypes = new ArrayList<>(List.of(DamageType.IN_FIRE, DamageType.ON_FIRE, DamageType.CAMPFIRE, DamageType.LAVA, DamageType.FIREBALL, DamageType.UNATTRIBUTED_FIREBALL, DamageType.DRY_OUT, DamageType.HOT_FLOOR, DamageType.SULFUR_CUBE_HOT));

    // Connect to InfectionFunctions class
    private InfectionFunctions infectionFunctions;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getConsoleSender().sendMessage("[ApocalypseEvent] " + ChatColor.GOLD + "ApocalypseEvent has been " + ChatColor.GREEN + "enabled" + ChatColor.GOLD + "!");

        // Save and load config
        saveDefaultConfig();
        loadConfigValues();

        // Adds the InfectionFunctions class
        this.infectionFunctions = new InfectionFunctions(this);

        // Register the command
        Objects.requireNonNull(getCommand("apocalypseevent")).setExecutor(new ApocalypseEventCommand(this, infectionFunctions));

        // Register the tab completer
        Objects.requireNonNull(getCommand("apocalypseevent")).setTabCompleter(new ApocalypseEventTabCompleter());

        // Register listeners
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new BucketEmptyListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(this, infectionFunctions), this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDieListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemConsumeListener(this, infectionFunctions), this);
        getServer().getPluginManager().registerEvents(new EntityDamagedListener(this, infectionFunctions), this);
        getServer().getPluginManager().registerEvents(new EntityRegainHealthListener(this, infectionFunctions), this);
        getServer().getPluginManager().registerEvents(new PrepareSmithingListener(this), this);
        getServer().getPluginManager().registerEvents(new PrepareEnchantListener(this), this);
        getServer().getPluginManager().registerEvents(new WeatherChangeListener(this), this);
        getServer().getPluginManager().registerEvents(new PrepareCraftListener(this), this);

        // Call function to register the custom recipes
        registerRecipes();
    }

    // Loads the config values
    public void loadConfigValues(){
        // Reload config
        super.reloadConfig();

        // Set public ints
        heatThreshold = getConfig().getInt("heat-threshold", 12);
        deathMessageRange = getConfig().getInt("death-message-range", 20);
        chatMessageRange = getConfig().getInt("chat-message-range", 20);
        infectTime = getConfig().getInt("infect-time", 300);
        juiceDuration = getConfig().getInt("juice-duration", 90);
        lightningFrequency = getConfig().getInt("lightning-frequency", 5);

        // Set public Booleans
        enabled = getConfig().getBoolean("enabled", true);
        limitWater = getConfig().getBoolean("limit-water", true);
        heatEnabled = getConfig().getBoolean("heat-enabled", true);
        limitDeathMessages = getConfig().getBoolean("limit-death-messages", true);
        limitChatMessages = getConfig().getBoolean("limit-chat-messages", true);
        dropDriedGhasts = getConfig().getBoolean("drop-dried-ghasts", true);
        dropArmorToFire = getConfig().getBoolean("drop-armor-to-fire", true);
        infection = getConfig().getBoolean("infection", true);
        fireOnlyDeathInfected = getConfig().getBoolean("fire-only-death-infected", true);
        customRecipes = getConfig().getBoolean("custom-recipes", true);
        lightningStorms = getConfig().getBoolean("lightning-storms", true);
        randomLightning = getConfig().getBoolean("random-lightning", false);
        unbreakingArmorCraft = getConfig().getBoolean("unbreaking-armor-craft", true);

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
        Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();

        // Check if the player is in a cold biome and needs freezing ticks
        if(freezingBiomes.contains(player.getLocation().getBlock().getBiome()))
            applyPlayerFreeze(player);

        // Apply player heat if enabled
        if(heatEnabled)
            startPlayerHeat(player);

        // Check if infection is enabled and the player has infection time
        Objective objective = scoreboard.getObjective("infectCount");
        if(infection && objective != null && objective.getScore(player.getName()).getScore() > 0)
            applyInfectionTime(player);
    }

    // Function to remove repeating tasks
    public void removeRepeatingTasks(Player player)
    {
        UUID uuid = player.getUniqueId();

        // Freezing ticks
        if(freezeTickTasks.containsKey(uuid))
            freezeTickTasks.get(uuid).cancel();
        freezeTickTasks.remove(uuid);

        // Heat
        if(applyHeatTasks.containsKey(uuid))
            applyHeatTasks.get(uuid).cancel();
        applyHeatTasks.remove(uuid);

        // Infection
        if(infectTasks.containsKey(uuid))
            infectTasks.get(uuid).cancel();
        infectTasks.remove(uuid);
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
                            heat++;

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

                                    // If the player is infected set them on fire and not in water or storming/thundering
                                    if(Objects.equals(player.getPersistentDataContainer().get(new NamespacedKey(ApocalypseEvent.this, "infected"), PersistentDataType.BOOLEAN), true) && !player.isInWater() && !(player.getWorld().hasStorm() || player.getWorld().isThundering()))
                                        player.setFireTicks(160);
                                }
                            }
                        }
                        else // Slightly reduce heat during the night
                        {
                            heat--;
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

                        // Remove some heat if their armor has the wind burst enchantment
                        for(ItemStack item : player.getInventory().getArmorContents())
                            if(item != null && item.hasItemMeta())
                                if(Objects.requireNonNull(item.getItemMeta()).hasEnchant(Enchantment.WIND_BURST))
                                    heat--;

                        // Remove some heat if in water
                        if(player.isInWater())
                            heat--;

                        // Slightly remove some heat during storms
                        if(player.getWorld().hasStorm() || player.getWorld().isThundering())
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

    // Function to schedule the repeating infection task
    public void applyInfectionTime(Player player)
    {
        // Declare variables
        final boolean startedWithRaidOmen;
        int time;

        // Get if the player has raid omen or not and base time off of that
        startedWithRaidOmen = player.hasPotionEffect(PotionEffectType.RAID_OMEN);
        if(startedWithRaidOmen)
            time = 3;
        else
            time = 1;

        // Schedule a repeating task to decrease infection
        infectTasks.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                // Checks if the player is online, exists, and is alive
                if(enabled && infection && player.isOnline() && player.isValid() && !player.isDead()) {
                    // Checks if the player's effect status has changed
                    if(player.hasPotionEffect(PotionEffectType.RAID_OMEN) != startedWithRaidOmen)
                    {
                        // Remove the current repeating task
                        infectTasks.remove(player.getUniqueId());
                        this.cancel();

                        // Recall applyInfectionTime to allow for a new delay on the task
                        applyInfectionTime(player);
                        return;
                    }

                    // Change the score of the player
                    Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
                    Objective objective = scoreboard.getObjective("infectCount");

                    // Checks if the objective exists
                    if(objective == null)
                    {
                        // Remove the current repeating task
                        infectTasks.remove(player.getUniqueId());
                        this.cancel();

                        // Create the objective via InfectionFunctions
                        infectionFunctions.StartInfection(player);
                    }
                    else
                    {
                        int score = objective.getScore(player.getName()).getScore();

                        // Checks if this would be the end of the score
                        if(score - 1 <= 0)
                        {
                            // Attempt to clear the score
                            String scoreReset = "scoreboard players reset " + player.getName() + " " + objective.getName();
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), scoreReset);

                            // Call convert function and remove task
                            infectionFunctions.ConvertToInfected(player);
                            infectTasks.remove(player.getUniqueId());
                            this.cancel();
                        }
                        else
                        {
                            // Decreases the score by 1
                            objective.getScore(player.getName()).setScore(score - 1);
                        }
                    }
                }
                else // Cancels the event when it is no longer valid
                {
                    infectTasks.remove(player.getUniqueId());
                    this.cancel();
                }
            }
        }.runTaskTimer(this, 0L, time * 20L)); // The 0 is when to start and the 20 is how many ticks later to run again (time in seconds)
    }

    // Function to register all custom recipes
    public void registerRecipes()
    {
        // Checks if the plugin and custom recipes are enabled
        if(!enabled || !customRecipes)
            return;

        // Define the water bottle item stack
        ItemStack waterBottle = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta)waterBottle.getItemMeta();
        if(meta != null) {
            meta.setBasePotionType(PotionType.WATER);
            waterBottle.setItemMeta(meta);
        }

        // Define Cool Juice item
        ItemStack coolJuice = new ItemStack(Material.POTION);
        meta = (PotionMeta)coolJuice.getItemMeta();
        if(meta != null) {
            meta.setDisplayName("§5Cool Juice");
            meta.addCustomEffect(new PotionEffect(PotionEffectType.WIND_CHARGED, juiceDuration * 20, 0, false),false);
            coolJuice.setItemMeta(meta);
        }

        // Cool Juice recipe
        ShapedRecipe coolJuiceRecipe = new ShapedRecipe(new NamespacedKey(this, "cool_juice"), coolJuice);
        coolJuiceRecipe.shape("XXX", "XYX", "XXX");
        coolJuiceRecipe.setIngredient('X', Material.CACTUS);
        coolJuiceRecipe.setIngredient('Y', new RecipeChoice.ExactChoice(waterBottle));

        // Define Sweet Juice item
        ItemStack sweetJuice = new ItemStack(Material.POTION);
        meta = (PotionMeta)sweetJuice.getItemMeta();
        if(meta != null) {
            meta.setDisplayName("§cSweet Juice");
            meta.addCustomEffect(new PotionEffect(PotionEffectType.RAID_OMEN, juiceDuration * 20, 0, false),false);
            sweetJuice.setItemMeta(meta);
        }

        // Sweet Juice recipe
        ShapedRecipe sweetJuiceRecipe = new ShapedRecipe(new NamespacedKey(this, "sweet_juice"), sweetJuice);
        sweetJuiceRecipe.shape("XXX", "XYX", "XXX");
        sweetJuiceRecipe.setIngredient('X', Material.SWEET_BERRIES);
        sweetJuiceRecipe.setIngredient('Y', new RecipeChoice.ExactChoice(waterBottle));

        // Register the recipes
        Bukkit.addRecipe(coolJuiceRecipe);
        Bukkit.addRecipe(sweetJuiceRecipe);
    }

    // Function to remove the custom recipes
    public void unregisterRecipes()
    {
        Bukkit.removeRecipe(new NamespacedKey(this, "cool_juice"));
        Bukkit.removeRecipe(new NamespacedKey(this, "sweet_juice"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getConsoleSender().sendMessage("[ApocalypseEvent] " + ChatColor.GOLD + "ApocalypseEvent has been " + ChatColor.RED + "disabled" + ChatColor.GOLD + "!");
    }
}
