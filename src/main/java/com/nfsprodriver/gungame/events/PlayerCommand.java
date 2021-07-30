package com.nfsprodriver.gungame.events;

import com.nfsprodriver.gungame.game.GunGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class PlayerCommand implements Listener {
    private final JavaPlugin plugin;
    private final Map<String, GunGame> games;

    public PlayerCommand(JavaPlugin plugin, Map<String, GunGame> games) {
        this.plugin = plugin;
        this.games = games;
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        if (command.equals("/stop") || command.equals("/restart")) {
            games.values().forEach(GunGame::stopGame);
        }
    }
}
