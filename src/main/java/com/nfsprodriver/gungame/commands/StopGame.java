package com.nfsprodriver.gungame.commands;

import com.nfsprodriver.gungame.game.GunGame;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class StopGame implements CommandExecutor {
    private final Map<String, GunGame> games;

    public StopGame(Map<String, GunGame> games) {
        this.games = games;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        String gameName = args[0];
        GunGame game = games.get(gameName);
        if (game != null) {
            game.stopGame();
            return true;
        }

        return false;
    }
}
