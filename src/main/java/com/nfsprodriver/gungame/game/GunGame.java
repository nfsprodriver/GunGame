package com.nfsprodriver.gungame.game;

import com.nfsprodriver.gungame.abstracts.Area;
import com.nfsprodriver.gungame.functions.General;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.*;
import org.bukkit.util.BoundingBox;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GunGame {
    private final JavaPlugin plugin;
    private final Integer maxLevel;
    private final BukkitScheduler scheduler;
    private final ScoreboardManager scoreboardManager;
    private final FileConfiguration config;
    public final Area area;
    public List<Player> playersInGame = new ArrayList<>();
    public List<Zombie> currentZombies = new ArrayList<>();
    public List<Sign> connectedSigns = new ArrayList<>();
    public Map<Player, List<ItemStack>> savedInventories = new HashMap<>();
    public Scoreboard scoreboard;
    public String name;
    public UUID uuid;
    private Integer timer = 0;

    public GunGame(JavaPlugin plugin, Area area, BukkitScheduler scheduler, String name) {
        this.plugin = plugin;
        this.area = area;
        this.scheduler = scheduler;
        this.scoreboardManager = plugin.getServer().getScoreboardManager();
        this.config = plugin.getConfig();
        this.name = name;
        this.maxLevel = config.getInt("ggareas." + name + ".options.maxLevel");
        this.uuid = UUID.randomUUID();
    }

    public void init() {
        generateScoreboard();
        scheduler.runTaskTimer(plugin, () -> {
            updateSigns();
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                if (playerIsInGame(player)) {
                    if (!(playersInGame.contains(player))) {
                        playersInGame.add(player);
                        playerScoreboard(player);
                    }
                } else {
                    if (playersInGame.contains(player)) {
                        playerLeaveGame(player);
                    }
                }
            });
            if (playersInGame.size() > 0) {
                timer++;
                checkPlayersInventory();
                updateActionBar();
            } else {
                if (timer > 0) {
                    stopGame();
                }
            }
        }, 20L, 20L);
    }

    private boolean playerIsInGame(Player player) {
        return this.area.locIsInArea(player.getLocation());
    }

    private void checkPlayersInventory() {
        playersInGame.forEach(player -> {
            PlayerInventory playerInv = player.getInventory();
            boolean hasWeapon = false;
            NamespacedKey gameUuidKey = new NamespacedKey(plugin, "gameUuid");
            List<ItemStack> invStacks = savedInventories.getOrDefault(player, new ArrayList<>());
            for (ItemStack invStack : playerInv.getContents()) {
                if (invStack == null || invStack.getItemMeta() == null) {
                    continue;
                }
                String gameUuid = invStack.getItemMeta().getPersistentDataContainer().get(gameUuidKey, PersistentDataType.STRING);
                if (gameUuid == null || !(gameUuid.equals(uuid.toString()))) {
                    invStacks.add(invStack);
                    playerInv.removeItem(invStack);
                } else if (invStack.getType().name().endsWith("_SWORD") || invStack.getType().name().endsWith("_AXE")) {
                    hasWeapon = true;
                }
            }
            savedInventories.put(player, invStacks);
            if (!hasWeapon) {
                ItemStack woodenSword = new ItemStack(Material.WOODEN_SWORD, 1);
                ItemMeta swordMeta = woodenSword.getItemMeta();
                assert swordMeta != null;
                swordMeta.setDisplayName("Basic sword");
                swordMeta.getPersistentDataContainer().set(gameUuidKey, PersistentDataType.STRING, uuid.toString());
                woodenSword.setItemMeta(swordMeta);
                playerInv.addItem(woodenSword);
            }
        });
    }

    private void updateActionBar() {
        playersInGame.forEach(player -> {
            int hours = timer / 3600;
            int minutes = (timer % 3600) / 60;
            int seconds = timer % 60;

            String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            String actionBarText = "Game time: " + timeString;
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionBarText));
        });
    }

    public void stopGame() {
        playersInGame.forEach(this::resetPlayer);
        uuid = UUID.randomUUID();
        timer = 0;
        playersInGame.clear();
        removeDroppedItems();
    }

    private void resetPlayer (Player player) {
        plugin.getLogger().info(player.getName() + " left the game \"Zombie Land " + name + "\"");
        NamespacedKey areaLivesKey = new NamespacedKey(plugin, "ggLives" + name);
        player.getPersistentDataContainer().remove(areaLivesKey);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
        player.setScoreboard(scoreboardManager.getNewScoreboard());
        Location loc = player.getLocation();
        Location spawnLoc = new General(plugin).goToSpawnEntry(loc);
        player.teleport(spawnLoc);
        player.getInventory().clear();
        giveBackInventory(player);
        NamespacedKey playerGameMoneyKey = new NamespacedKey(plugin, uuid + "_money");
        new General(plugin).givePlayerEmeralds(player, playerGameMoneyKey);
    }

    public void playerLeaveGame (Player player) {
        resetPlayer(player);
        playersInGame.remove(player);
    }

    public void giveBackInventory(Player player) {
        List<ItemStack> invStacks = savedInventories.get(player);
        if (invStacks != null) {
            invStacks.forEach(invStack -> {
                PlayerInventory playerInv = player.getInventory();
                playerInv.addItem(invStack);
            });
            savedInventories.remove(player);
        }
    }

    private void generateScoreboard() {
        scoreboard = scoreboardManager.getNewScoreboard();
        Set<String> teamNames = Objects.requireNonNull(config.getConfigurationSection("teams")).getKeys(false);
        teamNames.forEach(teamName -> scoreboard.registerNewTeam(teamName));
        Objective objective = scoreboard.registerNewObjective("gunGame" + name, "dummy", "GunGame " + name);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("GunGame " + name);
    }

    private void playerScoreboard(Player player) {
        Scoreboard playerSb = player.getScoreboard();
        if (playerSb != scoreboard) {
            NamespacedKey teamKey = new NamespacedKey(plugin, "team");
            String teamName = player.getPersistentDataContainer().get(teamKey, PersistentDataType.STRING);
            if (teamName == null) {
                teamName = (String) Objects.requireNonNull(config.getConfigurationSection("teams")).getKeys(false).toArray()[0];
            }
            Set<Team> teams = scoreboard.getTeams();
            String finalTeamName = teamName;
            Team playerTeam = teams.stream().filter(team -> team.getName().equals(finalTeamName)).collect(Collectors.toList()).get(0);
            playerTeam.setAllowFriendlyFire(false);
            playerTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);
            playerTeam.addPlayer(player);
            playerTeam.setDisplayName(teamName);
            NamespacedKey playerGameMoneyKey = new NamespacedKey(plugin, uuid + "_money");
            Integer playerGameMoney = player.getPersistentDataContainer().get(playerGameMoneyKey, PersistentDataType.INTEGER);
            if (playerGameMoney == null) {
                playerGameMoney = 0;
            }
            Score score = Objects.requireNonNull(scoreboard.getObjective("gunGame" + name)).getScore(player.getName() + " money");
            score.setScore(playerGameMoney);
            player.setScoreboard(scoreboard);
        }
    }

    private void updateSigns() {
        connectedSigns.forEach(connectedSign -> {
            int playersCount = playersInGame.size();
            connectedSign.setLine(1, "Players: " + playersCount);
            connectedSign.update();
        });
    }

    private void removeDroppedItems() {
        BoundingBox boundingBox = new BoundingBox(area.loc1.getX(), 0.0, area.loc1.getZ(), area.loc2.getX(), 256.0, area.loc2.getZ());
        Objects.requireNonNull(area.loc1.getWorld()).getNearbyEntities(boundingBox, (entity -> entity instanceof Item)).forEach(Entity::remove);
    }
}
