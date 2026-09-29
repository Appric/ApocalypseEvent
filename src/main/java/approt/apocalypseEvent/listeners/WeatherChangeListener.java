package approt.apocalypseEvent.listeners;

import approt.apocalypseEvent.ApocalypseEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class WeatherChangeListener implements Listener {

    // Connect to the main class
    private final ApocalypseEvent plugin;

    public WeatherChangeListener(ApocalypseEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event)
    {
        // Check if the plugin is enabled
        if (!plugin.enabled)
            return;

        // Checks if lightning storms are enabled
        if(plugin.lightningStorms) {
            // Checks if changing to rain/snow or thunder
            if (event.toWeatherState()) {
                Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    // If the weather is clear stop executing
                    if(event.getWorld().isClearWeather())
                        return;

                    // Find all players exposed to air
                    List<UUID> exposedPlayers = new ArrayList<>();
                    for(Player player : Bukkit.getOnlinePlayers())
                    {
                        // Check if they are exposed to the sky
                        if(player.getGameMode() != GameMode.SPECTATOR && player.getGameMode() != GameMode.CREATIVE && player.getLocation().getY() >= player.getWorld().getHighestBlockYAt(player.getLocation()))
                            exposedPlayers.add(player.getUniqueId());
                    }

                    if(!exposedPlayers.isEmpty()) {
                        if (plugin.randomLightning) {
                            // Pick a random player from exposed players
                            UUID uuid = exposedPlayers.get(ThreadLocalRandom.current().nextInt(exposedPlayers.size()));
                            Player player = Bukkit.getPlayer(uuid);


                            // Strike lightning at the player's location
                            if (player != null)
                                player.getWorld().strikeLightning(player.getLocation());
                        } else {
                            // Strike all exposed players
                            for (UUID uuid : exposedPlayers) {
                                Player player = Bukkit.getPlayer(uuid);

                                // Strike lightning at the player's location
                                if (player != null)
                                    player.getWorld().strikeLightning(player.getLocation());
                            }
                        }
                    }
                }, 200L, plugin.lightningFrequency * 20L); // Repeats lightningFrequency seconds
            }
        }
    }
}
