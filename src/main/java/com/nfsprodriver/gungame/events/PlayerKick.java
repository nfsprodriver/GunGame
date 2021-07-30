package com.nfsprodriver.gungame.events;

import com.nfsprodriver.gungame.game.GunGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class PlayerKick implements Listener {
    private final JavaPlugin plugin;
    private final Map<String, GunGame> games;

    public PlayerKick(JavaPlugin plugin, Map<String, GunGame> games) {
        this.plugin = plugin;
        this.games = games;
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        games.values().forEach(game -> {
            if (game.playersInGame.contains(player)) {
                game.playerLeaveGame(player);
            }
        });
    }
}
