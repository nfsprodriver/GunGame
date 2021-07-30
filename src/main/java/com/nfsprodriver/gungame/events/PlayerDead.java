package com.nfsprodriver.gungame.events;

import com.nfsprodriver.gungame.game.GunGame;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class PlayerDead implements Listener {
    //ln(level)^2

    private final JavaPlugin plugin;
    private final Map<String, GunGame> games;

    public PlayerDead(JavaPlugin plugin, Map<String, GunGame> games) {
        this.plugin = plugin;
        this.games = games;
    }

    @EventHandler
    public void onPlayerDead(PlayerDeathEvent event) {
        Player player = event.getEntity();
        NamespacedKey levelKey = new NamespacedKey(this.plugin, "gunGameLevel");
        Integer level = player.getPersistentDataContainer().get(levelKey, PersistentDataType.INTEGER);
        assert level != null;
        level = level - Math.toIntExact(Math.round(Math.pow(Math.log(level), 2.0)));
        player.getPersistentDataContainer().set(levelKey, PersistentDataType.INTEGER, level);

        NamespacedKey lastHitPlayerKey = new NamespacedKey(this.plugin, "lastHitPlayer");
        String lastHitPlayer = player.getPersistentDataContainer().get(lastHitPlayerKey, PersistentDataType.STRING);
        NamespacedKey lastHitTimeKey = new NamespacedKey(this.plugin, "lastHitTime");
        Long lastHitTime = player.getPersistentDataContainer().get(lastHitTimeKey, PersistentDataType.LONG);
        if (lastHitTime == null) {
            lastHitTime = 0L;
        }
        Long currentTime = System.currentTimeMillis();
        if (lastHitPlayer != null && (currentTime - lastHitTime) < 3000) {
            //TODO get player and if is in game...
        }
    }
}
