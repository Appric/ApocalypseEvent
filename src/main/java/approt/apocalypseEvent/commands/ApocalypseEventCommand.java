package approt.apocalypseEvent.commands;

import approt.apocalypseEvent.ApocalypseEvent;
import approt.apocalypseEvent.InfectionFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class ApocalypseEventCommand implements CommandExecutor {

    // Connect to the main and InfectionFunctions classes
    private final ApocalypseEvent plugin;
    private final InfectionFunctions infectionFunctions;
    public ApocalypseEventCommand(ApocalypseEvent plugin, InfectionFunctions infectionFunctions) {this.plugin = plugin; this.infectionFunctions = infectionFunctions;}

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args){
        // Checks the amount of arguments supplied
        if(args.length < 1 || args.length > 3)
            return false;

        // Checks for the reload command and proper permissions
        if(args[0].equals("reload") && sender.hasPermission("apocalypseevent.reload"))
        {
            // Load the new config values
            plugin.loadConfigValues();

            // Unregister then register the recipes based on the new config values
            plugin.unregisterRecipes();
            plugin.registerRecipes();

            // Refresh commands
            for(Player temp : Bukkit.getOnlinePlayers())
                temp.updateCommands();

            // Updated online infected player's health
            if(plugin.lessInfectedHealth > -1) {
                for (Player update : Bukkit.getOnlinePlayers()) {
                    if(Objects.equals(update.getPersistentDataContainer().get(new NamespacedKey(plugin, "infected"), PersistentDataType.BOOLEAN), true)) {
                        // Check and set how much maximum health infected players lose
                        AttributeInstance attribute = update.getAttribute(Attribute.MAX_HEALTH);
                        if (attribute != null) {
                            attribute.setBaseValue(attribute.getDefaultValue() - plugin.lessInfectedHealth);
                        }
                    }
                }
            }

            // Send the reloaded message
            sender.sendMessage("§6[ApocalypseEvent] §aThe config has been reloaded!§r");
        }
        // Checks for the toggle command and proper permissions
        else if(args[0].equals("toggle") && sender.hasPermission("apocalypseevent.toggle"))
        {
            // Check if already enabled or not
            if(plugin.enabled)
                plugin.getConfig().set("enabled", false);
            else
                plugin.getConfig().set("enabled", true);

            // Save updated value
            plugin.saveConfig();
            plugin.loadConfigValues();

            // Refresh commands
            for(Player temp : Bukkit.getOnlinePlayers())
                temp.updateCommands();

            if(plugin.enabled) {
                // Reinstate repeating tasks for all online players
                for(Player player : Bukkit.getOnlinePlayers())
                    plugin.reinstateRepeatingTasks(player);

                // Register the recipes
                plugin.registerRecipes();

                // Send enabled message
                sender.sendMessage("§6[ApocalypseEvent] §aThe plugin has been enabled!§r");
            }
            else {
                // Unregister the recipes
                plugin.unregisterRecipes();

                // Send disabled message
                sender.sendMessage("§6[ApocalypseEvent] §cThe plugin has been disabled!§r");
            }
        }
        // Checks for the infect command and proper permissions
        else if(args[0].equals("infect") && sender.hasPermission("apocalypseevent.infect"))
        {
            // Check for another argument
            if(args.length < 2) {
                sender.sendMessage("§6[ApocalypseEvent] §cYou must supply a player!§r");
                return true;
            }

            // Check if the player exists and is online
            Player player = Bukkit.getPlayerExact(args[1]);
            if(player == null)
            {
                sender.sendMessage("§6[ApocalypseEvent] §cThis player does not exist or is not online!§r");
                return true;
            }

            // Checks if the player is immune
            if(Objects.equals(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "immune"), PersistentDataType.BOOLEAN), true))
            {
                sender.sendMessage("§6[ApocalypseEvent] §cThis player is immune!§r");
                return true;
            }

            // Call the infection function
            infectionFunctions.StartInfection(player);
        }
        // Checks for the cure command and proper permissions
        else if(args[0].equals("cure") && sender.hasPermission("apocalypseevent.cure"))
        {
            // Check for another argument
            if(args.length < 2) {
                sender.sendMessage("§6[ApocalypseEvent] §cYou must supply a player!§r");
                return true;
            }

            // Check if the player exists and is online
            Player player = Bukkit.getPlayerExact(args[1]);
            if(player == null)
            {
                sender.sendMessage("§6[ApocalypseEvent] §cThis player does not exist or is not online!§r");
                return true;
            }

            // Call cure function
            infectionFunctions.CureInfected(player);
        }
        // Checks for the convert command and proper permissions
        else if(args[0].equals("convert") && sender.hasPermission("apocalypseevent.convert"))
        {
            // Check for another argument
            if(args.length < 2) {
                sender.sendMessage("§6[ApocalypseEvent] §cYou must supply a player!§r");
                return true;
            }

            // Check if the player exists and is online
            Player player = Bukkit.getPlayerExact(args[1]);
            if(player == null)
            {
                sender.sendMessage("§6[ApocalypseEvent] §cThis player does not exist or is not online!§r");
                return true;
            }

            // Checks if the player is immune
            if(Objects.equals(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "immune"), PersistentDataType.BOOLEAN), true))
            {
                sender.sendMessage("§6[ApocalypseEvent] §cThis player is immune!§r");
                return true;
            }

            // Checks if the player is already infected
            if(Objects.equals(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "infected"), PersistentDataType.BOOLEAN), true))
            {
                sender.sendMessage("§6[ApocalypseEvent] §cThis player is already infected!§r");
                return true;
            }

            // Call convert function
            infectionFunctions.ConvertToInfected(player);
        }
        // Checks for the remedy command and proper permissions
        else if(args[0].equals("remedy") && sender.hasPermission("apocalypseevent.remedy"))
        {
            // Check for another argument
            if(args.length < 2) {
                sender.sendMessage("§6[ApocalypseEvent] §cYou must supply a player!§r");
                return true;
            }

            // Check if the player exists and is online
            Player player = Bukkit.getPlayerExact(args[1]);
            if(player == null)
            {
                sender.sendMessage("§6[ApocalypseEvent] §cThis player does not exist or is not online!§r");
                return true;
            }

            // Check if an amount was specified, and if so is valid
            int amount = 1;
            if(args.length == 3)
            {
                try{
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException _) {
                }
            }

            // Define the remedy item
            ItemStack remedy = new ItemStack(Material.HONEY_BOTTLE);
            ItemMeta meta = remedy.getItemMeta();
            if(meta != null)
            {
                meta.setDisplayName("§6§lThe Cure§r");
                meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "remedy-item"), PersistentDataType.BOOLEAN, true);
                meta.setEnchantmentGlintOverride(true);
                remedy.setItemMeta(meta);
            }
            remedy.setAmount(amount);

            // Give the player the item
            player.getInventory().addItem(remedy);
        }
        // Checks for the immunize command and proper permissions
        else if(args[0].equals("immunize") && sender.hasPermission("apocalypseevent.immunize"))
        {
            // Check for another argument
            if(args.length < 2) {
                sender.sendMessage("§6[ApocalypseEvent] §cYou must supply a player!§r");
                return true;
            }

            // Check if the player exists and is online
            Player player = Bukkit.getPlayerExact(args[1]);
            if(player == null)
            {
                sender.sendMessage("§6[ApocalypseEvent] §cThis player does not exist or is not online!§r");
                return true;
            }

            // Checks if the player is immune
            if(Objects.equals(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "immune"), PersistentDataType.BOOLEAN), true))
            {
                sender.sendMessage("§6[ApocalypseEvent] §cThis player is already immune!§r");
                return true;
            }

            // Call immunize function
            infectionFunctions.Immunize(player);
        }
        // Checks for the deimmunize command and proper permissions
        else if(args[0].equals("deimmunize") && sender.hasPermission("apocalypseevent.deimmunize"))
        {
            // Check for another argument
            if(args.length < 2) {
                sender.sendMessage("§6[ApocalypseEvent] §cYou must supply a player!§r");
                return true;
            }

            // Check if the player exists and is online
            Player player = Bukkit.getPlayerExact(args[1]);
            if(player == null)
            {
                sender.sendMessage("§6[ApocalypseEvent] §cThis player does not exist or is not online!§r");
                return true;
            }

            // Checks if the player is not immune
            if(Objects.equals(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "immune"), PersistentDataType.BOOLEAN), false))
            {
                sender.sendMessage("§6[ApocalypseEvent] §cThis player is not immune!§r");
                return true;
            }

            // Call deimmunize function
            infectionFunctions.Deimmunize(player);
        }
        // Checks for the remedy command and proper permissions
        else if(args[0].equals("spear") && sender.hasPermission("apocalypseevent.spear"))
        {
            // Check for another argument
            if(args.length < 2) {
                sender.sendMessage("§6[ApocalypseEvent] §cYou must supply a player!§r");
                return true;
            }

            // Check if the player exists and is online
            Player player = Bukkit.getPlayerExact(args[1]);
            if(player == null)
            {
                sender.sendMessage("§6[ApocalypseEvent] §cThis player does not exist or is not online!§r");
                return true;
            }

            // Check if an amount was specified, and if so is valid
            int amount = 1;
            if(args.length == 3)
            {
                try{
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException _) {
                }
            }

            // Define the remedy item
            ItemStack surgeSpear = new ItemStack(Material.COPPER_SPEAR);
            ItemMeta meta = surgeSpear.getItemMeta();
            if(meta != null)
            {
                meta.setDisplayName("§x§0§0§F§F§F§5§lT§x§2§4§F§F§D§2§lh§x§4§8§F§F§A§F§le §x§9§0§F§F§6§9§lS§x§B§4§F§F§4§6§lu§x§D§8§F§F§2§3§lr§x§F§C§F§F§0§0§lg§le§r");
                meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "surge-spear"), PersistentDataType.BOOLEAN, true);
                meta.addEnchant(Enchantment.CHANNELING, 1, false);
                surgeSpear.setItemMeta(meta);
            }
            surgeSpear.setAmount(amount);

            // Give the player the item
            player.getInventory().addItem(surgeSpear);
        }

        return true;
    }
}
