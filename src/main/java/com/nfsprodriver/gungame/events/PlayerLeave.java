package com.nfsprodriver.gungame.events;

import com.nfsprodriver.gungame.game.GunGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class PlayerLeave implements Listener {
    private final JavaPlugin plugin;
    private final Map<String, GunGame> games;

    public PlayerLeave(JavaPlugin plugin, Map<String, GunGame> games) {
        this.plugin = plugin;
        this.games = games;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        games.values().forEach(game -> {
            if (game.playersInGame.contains(player)) {
                game.playerLeaveGame(player);
            }
        });
    }
}
