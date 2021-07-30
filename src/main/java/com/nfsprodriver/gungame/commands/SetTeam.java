package com.nfsprodriver.gungame.commands;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Set;

public class SetTeam implements CommandExecutor {
    private final JavaPlugin plugin;

    public SetTeam(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String playerName = args[0];
        String teamName = args[1];
        if (!(playerName.isEmpty()) && !(teamName.isEmpty())) {
            Player player = plugin.getServer().getPlayer(playerName);
            assert player != null;
            Set<String> teamNames = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("teams")).getKeys(false);
            if ((!teamNames.contains(teamName))) {
                sender.sendMessage("Parse a valid team name!");
                return false;
            }
            NamespacedKey teamKey = new NamespacedKey(plugin, "team");
            player.getPersistentDataContainer().set(teamKey, PersistentDataType.STRING, teamName);

            return true;
        } else {
            sender.sendMessage("Parse a valid player and team name!");
        }

        return false;
    }
}
