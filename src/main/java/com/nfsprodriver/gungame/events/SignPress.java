package com.nfsprodriver.gungame.events;

import com.nfsprodriver.gungame.game.GunGame;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Objects;

public class SignPress implements Listener {
    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final Map<String, GunGame> games;

    public SignPress(JavaPlugin plugin, Map<String, GunGame> games) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.games = games;
    }

    @EventHandler
    public void onSignPress(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && clickedBlock != null && (clickedBlock.getType() == Material.OAK_SIGN || clickedBlock.getType() == Material.OAK_WALL_SIGN)) {
            Sign sign = (Sign) clickedBlock.getState();
            NamespacedKey signTypeKey = new NamespacedKey(plugin, "signType");
            String type = sign.getPersistentDataContainer().get(signTypeKey, PersistentDataType.STRING);
            Player player = event.getPlayer();
            Location playerLoc = player.getLocation();
            if (type != null && type.equals("gg")) {
                NamespacedKey ggAreaKey = new NamespacedKey(plugin, "ggArea");
                String area = sign.getPersistentDataContainer().get(ggAreaKey, PersistentDataType.STRING);
                if (enterGame(player, area)) {
                    playerLoc.setX(config.getInt("ggareas." + area + ".entry.x"));
                    playerLoc.setY(config.getInt("ggareas." + area + ".entry.y"));
                    playerLoc.setZ(config.getInt("ggareas." + area + ".entry.z"));
                    playerLoc.setWorld(plugin.getServer().getWorld(Objects.requireNonNull(config.getString("ggareas." + area + ".world"))));
                    player.teleport(playerLoc);
                } else {
                    player.sendMessage("No lives left for running game, please wait for next game!");
                }
            } else if(type.equals("spawn")) {
                games.values().forEach(game -> {
                    if (game.playersInGame.contains(player)) {
                        game.playerLeaveGame(player);
                    }
                });
            }
        }
    }

    private Boolean enterGame(Player player, String area) {
        NamespacedKey ggLivesKey = new NamespacedKey(plugin, "ggLives" + area);
        Integer ggLives = player.getPersistentDataContainer().get(ggLivesKey, PersistentDataType.INTEGER);
        if (ggLives != null) {
            return ggLives > 0;
        } else {
            GunGame game = games.get(area);
            assert game != null;
            if (game.playersBeenInGame.contains(player.getName())) {
                return false;
            }
            Integer playerLives = config.getInt("ggareas." + area + ".options.playerLives");
            player.getPersistentDataContainer().set(ggLivesKey, PersistentDataType.INTEGER, playerLives);
            return true;
        }
    }
}
