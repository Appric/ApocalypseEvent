package approt.apocalypseEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class ApocalypseEvent extends JavaPlugin {

    // Public variables
    public Boolean enabled;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "ApocalypseEvent has been enabled!");

        // Save and load config
        saveDefaultConfig();
        loadConfigValues();
    }

    public void loadConfigValues(){
        super.reloadConfig();
        enabled = getConfig().getBoolean("enabled", true);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "ApocalypseEvent has been disabled!");
    }
}
