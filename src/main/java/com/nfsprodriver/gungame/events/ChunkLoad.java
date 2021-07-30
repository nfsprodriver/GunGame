package com.nfsprodriver.gungame.events;

import com.nfsprodriver.gungame.functions.General;
import com.nfsprodriver.gungame.game.GunGame;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class ChunkLoad implements Listener {
    private JavaPlugin plugin;
    private Map<String, GunGame> games;

    public ChunkLoad(JavaPlugin plugin, Map<String, GunGame> games) {
        this.plugin = plugin;
        this.games = games;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        new General(plugin).chunkSigns(games, chunk);
    }
}
