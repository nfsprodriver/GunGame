package com.nfsprodriver.gungame.events;

import com.nfsprodriver.gungame.abstracts.Area;
import com.nfsprodriver.gungame.game.GunGame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Set;

public class WorldLoaded implements Listener {
    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private Map<String, GunGame> games;

    public WorldLoaded(JavaPlugin plugin, Map<String, GunGame> games) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.games = games;
    }

    @EventHandler
    public void onWorldLoaded(WorldLoadEvent event) {
        World world = event.getWorld();
        Location spawnLoc = world.getSpawnLocation();
        ConfigurationSection ggareas = config.getConfigurationSection("ggareas");
        assert ggareas != null;
        Set<String> ggareasKeys = ggareas.getKeys(false);
        ggareasKeys.forEach(ggareasKey -> {
            ConfigurationSection ggarea = ggareas.getConfigurationSection(ggareasKey);
            assert ggarea != null;
            if (world.getName().equals(ggarea.getString("world"))) {
                Area area = new Area(spawnLoc, ggarea.getDouble("x1"), ggarea.getDouble("x2"), ggarea.getDouble("z1"), ggarea.getDouble("z2"));
                GunGame game = new GunGame(plugin, area, Bukkit.getScheduler(), ggareasKey);
                games.put(ggareasKey, game);
                game.init();
            }
        });
    }
}
