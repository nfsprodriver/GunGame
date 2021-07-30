package com.nfsprodriver.gungame.events;

import com.nfsprodriver.gungame.functions.General;
import com.nfsprodriver.gungame.game.GunGame;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Set;

public class PlayerJoin implements Listener {
    private final JavaPlugin plugin;
    private final Map<String, GunGame> games;

    public PlayerJoin(JavaPlugin plugin, Map<String, GunGame> games) {
        this.plugin = plugin;
        this.games = games;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Set<NamespacedKey> dataKeys = player.getPersistentDataContainer().getKeys();
        dataKeys.forEach(dataKey -> {
            if (dataKey.getKey().endsWith("_money")) {
                String uuid = dataKey.getKey().split("_")[0];
                if (games.values().stream().noneMatch(game -> game.uuid.toString().equals(uuid))) {
                    new General(plugin).givePlayerEmeralds(player, dataKey);
                }
            }
        });
    }
}
