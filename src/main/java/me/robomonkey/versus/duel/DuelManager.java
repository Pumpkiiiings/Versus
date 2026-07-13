package me.robomonkey.versus.duel;

import me.robomonkey.versus.Versus;
import me.robomonkey.versus.arena.Arena;
import me.robomonkey.versus.arena.ArenaManager;
import me.robomonkey.versus.dependency.PAPIUtil;
import me.robomonkey.versus.duel.eventlisteners.*;
import me.robomonkey.versus.duel.playerdata.DataManager;
import me.robomonkey.versus.duel.playerdata.PlayerData;
import me.robomonkey.versus.duel.playerdata.PlayerStats;
import me.robomonkey.versus.duel.playerdata.StatsManager;
import me.robomonkey.versus.duel.request.RequestManager;
import me.robomonkey.versus.kit.Kit;
import me.robomonkey.versus.settings.Placeholder;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import me.robomonkey.versus.util.EffectUtil;
import me.robomonkey.versus.util.MessageUtil;
import me.robomonkey.versus.dependency.EconomyManager;
import me.robomonkey.versus.arena.ArenaRollbackManager;
import me.robomonkey.versus.duel.rewards.DuelReward;
import me.robomonkey.versus.duel.rewards.RewardManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DuelManager {
    private static DuelManager instance;
    private HashMap<UUID, Duel> duelistMap = new HashMap<>();
    private ArenaManager arenaManager = ArenaManager.getInstance();
    private DataManager dataManager;
    private Versus plugin = Versus.getInstance();
    private HashMap<UUID, Location> spectatorLocations = new HashMap<>();

    private DuelManager() {
        instance = this;
        dataManager = new DataManager();
        dataManager.loadDataMap();
        registerListeners();
    }

    public static DuelManager getInstance() {
        if (instance == null) {
            new DuelManager();
        }
        return instance;
    }

    public void addDuel(Duel duel) {
        duel.getPlayers().forEach(player -> duelistMap.put(player.getUniqueId(), duel));
    }

    public void unregisterFromDuel(Player player) {
        duelistMap.remove(player.getUniqueId());
    }

    private void removeDuel(Duel duel) {
        duelistMap.values().removeIf(value -> value.equals(duel));
    }

    public Duel getDuel(Player player) {
        return duelistMap.get(player.getUniqueId());
    }

    public void registerQuitter(Player quitter) {
        quitter.setHealth(0);
    }

    public boolean hasStoredData(Player player) {
        return dataManager.contains(player);
    }

    public boolean isDueling(Player player) {
        return duelistMap.containsKey(player.getUniqueId());
    }

    public boolean isMoving(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to.getX() != from.getX() || to.getY() != from.getY() || to.getZ() != from.getZ()) return true;
        return false;
    }

    public void restoreData(Player player, boolean isWinner) {
        if (!player.isOnline()) return;
        Versus.log("Attempting to restore data");
        if (!dataManager.contains(player)) return;
        PlayerData data = dataManager.extractData(player);
        player.setLevel(data.xpLevel);
        player.setExp(data.xpProgress);
        player.getInventory().setContents(data.items);
        // Restore gamemode to what it was before the duel
        if (data.gameMode != null) {
            player.setGameMode(data.gameMode);
        } else {
            player.setGameMode(org.bukkit.GameMode.SURVIVAL);
        }
        restoreLocation(player, data, isWinner);
    }

    private void restoreLocation(Player player, PlayerData data, Boolean isWinner) {
        ReturnOption returnOption = isWinner ? Settings.getReturnOption(Setting.RETURN_WINNERS) : Settings.getReturnOption(Setting.RETURN_LOSERS);
        switch (returnOption) {
            case SPAWN:
                player.teleport(player.getWorld().getSpawnLocation());
                break;
            case PREVIOUS:
                player.teleport(data.previousLocation.toLocation());
                break;
            case SPECTATE:
                Arena respawnArena = arenaManager.getArena(data.arenaName);
                if (respawnArena == null) player.teleport(player.getWorld().getSpawnLocation());
                else player.teleport(respawnArena.getSpectateLocation());
                break;
            case CUSTOM:
                Location customLocation = isWinner ? Settings.getLocation(Setting.WINNER_RETURN_LOCATION): Settings.getLocation(Setting.LOSER_RETURN_LOCATION);
                if(customLocation == null) {
                    Versus.log("Custom respawn location is improperly formatted. "+player.getName()+" will return to their previous location, instead.");
                    player.teleport(data.previousLocation.toLocation());
                    return;
                }
                player.teleport(customLocation);



        }
    }

    public void setupDuel(Player playerOne, Player playerTwo) {
        setupDuel(playerOne, playerTwo, arenaManager.getAvailableArena(), 0.0, me.robomonkey.versus.kit.KitManager.getInstance().getDefaultKit());
    }
    
    public void setupDuel(Player playerOne, Player playerTwo, Arena arena, double betAmount, me.robomonkey.versus.kit.Kit kit) {
        setupGroupDuel(List.of(playerOne), List.of(playerTwo), arena, betAmount, kit);
    }

    public void setupGroupDuel(List<Player> team1, List<Player> team2, Arena arena, double betAmount, me.robomonkey.versus.kit.Kit kit) {
        Duel newDuel = createNewDuel(team1, team2, arena, kit);
        newDuel.setBetAmount(betAmount);
        executeDuelSetup(newDuel, team1, team2);
    }
    
    public void registerDuel(List<Player> team1, List<Player> team2, me.robomonkey.versus.duel.betting.BettingSession session) {
        Arena arena = arenaManager.getArena(session.getArenaName());
        Duel newDuel = createNewDuel(team1, team2, arena, session.getKit());
        newDuel.setBettingSession(session);
        executeDuelSetup(newDuel, team1, team2);
    }
    
    private void executeDuelSetup(Duel newDuel, List<Player> team1, List<Player> team2) {
        newDuel.getPlayers().stream().forEach((player) -> dataManager.save(player, newDuel.getArena()));
        
        team1.forEach(p -> p.teleport(newDuel.getArena().getSpawnLocationOne()));
        team2.forEach(p -> p.teleport(newDuel.getArena().getSpawnLocationTwo()));
        
        newDuel.getPlayers().stream().forEach((player) -> {
            dataManager.update(player, DataManager.DataType.INVENTORY);
            dataManager.saveDataMap();
            groomForDuel(player);
        });
        populateKits(newDuel);
        newDuel.startCountdown(() -> commenceDuel(newDuel));
        newDuel.getPlayers().forEach(me.robomonkey.versus.arena.ArenaVisibilityManager::updateVisibility);
        if (newDuel.isPublic()) announceDuelStart(newDuel);
    }

    private void populateKits(Duel duel) {
        duel.getPlayers().forEach((player) -> {
            Kit kit = duel.getKit();
            if (kit != null) {
                player.getInventory().setContents(kit.getItems());
            }
        });
    }

    public void announceDuelStart(Duel duel) {
        String announcementMessage = Settings.getMessage(Setting.DUEL_START_ANNOUNCEMENT,
                Placeholder.of("%player_one%", PAPIUtil.getName(duel.getPlayers().get(0))),
                Placeholder.of("%player_two%", PAPIUtil.getName(duel.getPlayers().get(1))));
        String commandText = "/spectate " + duel.getPlayers().get(0).getName();
        TextComponent announcement;
        try {
            announcement = MessageUtil.getClickableMessageBetween(announcementMessage, commandText, commandText, "%button%");
        } catch (Exception e) {
            Versus.log("Config.yml option 'duel_start_announcement' is improperly configured. Please review. Using default value...");
            announcementMessage = Settings.getDefaultMessage(Setting.DUEL_START_ANNOUNCEMENT,
                    Placeholder.of("%player_one%", PAPIUtil.getName(duel.getPlayers().get(0))),
                    Placeholder.of("%player_two%", PAPIUtil.getName(duel.getPlayers().get(1))));
            announcement = MessageUtil.getClickableMessageBetween(announcementMessage, commandText, commandText, "%button%");
        }
        Bukkit.spigot().broadcast(announcement);
    }

    public void announceDuelEnd(Duel duel) {
        if (duel.getWinners().isEmpty() || duel.getLosers().isEmpty()) return;
        
        String winners = String.join(", ", duel.getWinners().stream().map(u -> Bukkit.getPlayer(u).getName()).toArray(String[]::new));
        String losers = String.join(", ", duel.getLosers().stream().map(u -> Bukkit.getPlayer(u).getName()).toArray(String[]::new));
        
        String announcementMessage = Settings.getMessage(Setting.DUEL_END_ANNOUNCEMENT,
                Placeholder.of("%winner%", winners),
                Placeholder.of("%loser%", losers));
        Bukkit.broadcastMessage(announcementMessage);
    }


    public void registerMoveEvent(Player player, PlayerMoveEvent event) {
        Duel currentDuel = getDuel(player);
        if (currentDuel.getState() == DuelState.COUNTDOWN && isMoving(event)) {
            event.setCancelled(true);
        }
    }

    /**
     * Only call after checking that ensuring that the player is currently in a duel with duelManager.duelFromPlayer(..);
     */
    public void registerDuelistDeath(Player loser, boolean fakeDeath) {
        Duel currentDuel = getDuel(loser);
        if (currentDuel.getState() == DuelState.COUNTDOWN) {
            undoCountdown(currentDuel);
        }
        if (fakeDeath) {
            restoreData(loser, false);
            resetAttributes(loser);
        }
        
        if (currentDuel.isActive()) {
            try {
                List<Player> team1 = currentDuel.getTeam1();
                List<Player> team2 = currentDuel.getTeam2();
                
                // Move player to spectate mode
                loser.setGameMode(GameMode.SPECTATOR);
                loser.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_DIED_SPECTATING)));
                
                // Check if entire team is dead
                boolean team1Alive = false;
                for (Player p : team1) {
                    if (p.isOnline() && p.getGameMode() == GameMode.SURVIVAL) {
                        team1Alive = true;
                        break;
                    }
                }
                
                boolean team2Alive = false;
                for (Player p : team2) {
                    if (p.isOnline() && p.getGameMode() == GameMode.SURVIVAL) {
                        team2Alive = true;
                        break;
                    }
                }
                
                if (!team1Alive) {
                    registerDuelCompletion(team2, team1, currentDuel);
                } else if (!team2Alive) {
                    registerDuelCompletion(team1, team2, currentDuel);
                }
            } catch (Exception e) {
                me.robomonkey.versus.Versus.error("An error occurred while processing player death in duel: " + e.getMessage());
                e.printStackTrace();
                // Force end the duel as a draw to prevent players getting stuck
                registerDuelCompletion(new java.util.ArrayList<>(), currentDuel.getPlayers(), currentDuel);
            }
        }
    }

    private void registerDuelCompletion(List<Player> winningTeam, List<Player> losingTeam, Duel duel) {
        duel.end(winningTeam, losingTeam);
        stopDuel(duel);
    }

    private void stopDuel(Duel duel) {
        if (duel.isActive()) return;
        arenaManager.removeDuel(duel);
        duel.getPlayers().stream().filter(Player::isOnline).forEach(Player::stopAllSounds);
        
        List<UUID> winners = duel.getWinners();
        List<UUID> losers = duel.getLosers();
        
        // Handle bet payout
        me.robomonkey.versus.duel.betting.BettingSession bettingSession = duel.getBettingSession();
        if (bettingSession != null) {
            double potMoney = bettingSession.getMoney(bettingSession.getPlayer1()) + bettingSession.getMoney(bettingSession.getPlayer2());
            int potXp = bettingSession.getXp(bettingSession.getPlayer1()) + bettingSession.getXp(bettingSession.getPlayer2());
            java.util.List<org.bukkit.inventory.ItemStack> potItems = new java.util.ArrayList<>();
            potItems.addAll(bettingSession.getItems(bettingSession.getPlayer1()));
            potItems.addAll(bettingSession.getItems(bettingSession.getPlayer2()));

            for (UUID w : winners) {
                Player winner = Bukkit.getPlayer(w);
                UUID loserUuid = losers.isEmpty() ? null : losers.get(0);

                // Pay money immediately (no risk of loss)
                if (potMoney > 0 && winner != null) {
                    EconomyManager.deposit(winner, potMoney);
                    winner.sendMessage(Settings.getMessage(Setting.DUEL_BET_WON, Placeholder.of("%amount%", EconomyManager.format(potMoney))));
                }

                // Items & XP → save to DB so winner claims with /duel rewards (anti-dupe)
                boolean hasItemsOrXp = !potItems.isEmpty() || potXp > 0;
                if (hasItemsOrXp && loserUuid != null) {
                    DuelReward reward = new DuelReward(
                            w, loserUuid, System.currentTimeMillis(),
                            new java.util.ArrayList<>(potItems), 0.0, potXp
                    );
                    RewardManager.getInstance().saveReward(reward);
                    if (winner != null) {
                        winner.sendMessage(Settings.getMessage(Setting.REWARDS_GUI_PENDING_NOTIFY));
                    }
                }
            }
        } else if (duel.getBetAmount() > 0) {
            // Fallback for legacy requests (group duels without betting session — money only)
            double potPerWinner = duel.getBetAmount() * 2;
            for (UUID w : winners) {
                Player winner = Bukkit.getPlayer(w);
                if (winner != null) {
                    EconomyManager.deposit(winner, potPerWinner);
                    winner.sendMessage(Settings.getMessage(Setting.DUEL_BET_WON, Placeholder.of("%amount%", EconomyManager.format(potPerWinner))));
                }
            }
        }

        // Handle stats
        for (UUID w : winners) {
            Player winner = Bukkit.getPlayer(w);
            if (winner != null) {
                PlayerStats winnerStats = StatsManager.getInstance().getStats(winner);
                winnerStats.addWin();
                winnerStats.addStreak();
                StatsManager.getInstance().savePlayer(winnerStats);
                
                if (Settings.is(Setting.STREAK_BROADCAST_ENABLED)) {
                    int minStreak = Settings.getNumber(Setting.STREAK_BROADCAST_MINIMUM);
                    int interval = Settings.getNumber(Setting.STREAK_BROADCAST_INTERVAL);
                    int streak = winnerStats.getCurrentStreak();
                    
                    if (streak >= minStreak && (streak - minStreak) % interval == 0) {
                        String msg = Settings.getMessage(Setting.STREAK_BROADCAST_MESSAGE,
                                Placeholder.of("%player%", PAPIUtil.getName(winner)),
                                Placeholder.of("%streak%", String.valueOf(streak)));
                        Bukkit.broadcastMessage(msg);
                    }
                }
                if (Settings.getInstance().getConfig().contains("dueling.commands_on_win")) {
                    for (String cmd : Settings.getInstance().getConfig().getStringList("dueling.commands_on_win")) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", winner.getName()));
                    }
                }
                
                renderWinEffects(winner, duel);
            }
        }
        
        for (UUID l : losers) {
            Player loser = Bukkit.getPlayer(l);
            if (loser != null) {
                PlayerStats loserStats = StatsManager.getInstance().getStats(loser);
                loserStats.addLoss();
                loserStats.resetStreak();
                StatsManager.getInstance().savePlayer(loserStats);
                if (Settings.getInstance().getConfig().contains("dueling.commands_on_loss")) {
                    for (String cmd : Settings.getInstance().getConfig().getStringList("dueling.commands_on_loss")) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", loser.getName()));
                    }
                }
                
                renderLossEffects(loser, winners);
                extricateLoser(loser, duel);
            }
        }
        
        announceDuelEnd(duel);
        
        // Return spectators & rollback arena
        ArenaRollbackManager.getInstance().rollback(duel.getArena().getName());
        returnSpectators(duel.getArena());
    }
    
    private void extricateLoser(Player player, Duel duel) {
        restoreData(player, false);
        resetAttributes(player);
        unregisterFromDuel(player);
        me.robomonkey.versus.arena.ArenaVisibilityManager.updateVisibility(player);
    }
    
    public void addSpectator(Player player, Duel duel) {
        spectatorLocations.put(player.getUniqueId(), player.getLocation());
        player.teleport(duel.getArena().getSpectateLocation());
        me.robomonkey.versus.arena.ArenaVisibilityManager.addSpectator(player, duel);
    }
    
    public boolean isSpectating(Player player) {
        return spectatorLocations.containsKey(player.getUniqueId());
    }
    
    public void removeSpectator(Player player) {
        Location returnLoc = spectatorLocations.remove(player.getUniqueId());
        if (returnLoc != null) {
            player.teleport(returnLoc);
            me.robomonkey.versus.arena.ArenaVisibilityManager.removeSpectator(player);
        }
    }
    
    public void returnSpectators(Arena arena) {
        // TP everyone out of the spectator area (approximate distance check)
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().equals(arena.getSpectateLocation().getWorld()) &&
                p.getLocation().distanceSquared(arena.getSpectateLocation()) < 2500) { // 50 block radius
                Location returnLoc = spectatorLocations.remove(p.getUniqueId());
                if (returnLoc != null) {
                    p.teleport(returnLoc);
                    me.robomonkey.versus.arena.ArenaVisibilityManager.removeSpectator(p);
                }
                else p.teleport(p.getWorld().getSpawnLocation());
            }
        }
    }

    private Duel createNewDuel(List<Player> team1, List<Player> team2, Arena arena, Kit kit) {
        if (arena == null) arena = arenaManager.getAvailableArena();
        Duel newDuel = new Duel(arena, team1, team2, kit);
        addDuel(newDuel);
        arenaManager.registerDuel(arena, newDuel);
        return newDuel;
    }

    private void commenceDuel(Duel duel) {
        duel.setState(DuelState.ACTIVE);
        handleStartEffects(duel);
    }

    private void handleStartEffects(Duel duel) {
        if (duel.isFireworksEnabled())
            EffectUtil.spawnFireWorks(duel.getArena().getCenterLocation(), 1, 10, duel.getFireworkColor());
        for (Player player : duel.getPlayers()) {
            EffectUtil.sendTitle(player, Settings.getMessage(Setting.DUEL_GO_MESSAGE), 20, true);
            player.setInvulnerable(false);
            if (duel.isFightMusicEnabled())
                player.playSound(player.getLocation(), duel.getFightMusic(), Float.POSITIVE_INFINITY, 2.0F);
        }
    }

    private void groomForDuel(Player player) {
        resetAttributes(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 10));
        player.setGameMode(GameMode.SURVIVAL);
        player.setInvulnerable(true);
        player.setAllowFlight(false);
        player.setLevel(0);
        player.setExp(0);
    }

    private void registerListeners() {
        List.of(new CommandListener(),
                        new JoinEventListener(),
                        new RespawnEventListener(),
                        new InteractEventListener(),
                        new DeathEventListener(),
                        new MoveEventListener(),
                        new QuitEventListener(),
                        new FireworkExplosionListener(),
                        new BlockBreakListener(),
                        new BlockPlaceListener(),
                        new DamageEventListener(),
                        new EntityTagListener())
                .forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, Versus.getInstance()));
    }

    private void undoCountdown(Duel duel) {
        duel.cancelCountdown();
        duel.getPlayers().forEach(EffectUtil::unfreezePlayer);
    }

    private void extricateWinner(Player player, Duel duel) {
        restoreData(player, true);
        resetAttributes(player);
        removeDuel(duel);
        me.robomonkey.versus.arena.ArenaVisibilityManager.updateVisibility(player);
        RequestManager.getInstance().notifyDuelCompletion();
    }

    private void resetAttributes(Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setInvulnerable(false);
        EffectUtil.unfreezePlayer(player);
        player.getActivePotionEffects().stream().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.setItemOnCursor(null);
        player.closeInventory();
        player.stopAllSounds();
    }

    private void renderWinEffects(Player winner, Duel duel) {
        extricateWinner(winner, duel);
        if (duel.isFireworksEnabled()) EffectUtil.spawnFireWorks(winner.getLocation(), 1, 50, duel.getFireworkColor());
        if (duel.isVictoryMusicEnabled()) EffectUtil.playSound(winner, duel.getVictorySong());
        winner.sendTitle(
                Settings.getMessage(Setting.VICTORY_TITLE_MESSAGE),
                Settings.getMessage(Setting.VICTORY_SUBTITLE_MESSAGE, Placeholder.of("%player%", PAPIUtil.getName(winner))), 20, 40, 20);
        
        if (duel.isVictoryEffectsEnabled() && duel.isFireworksEnabled()) {
            EffectUtil.spawnFireWorksDelayed(winner.getLocation(), 3, 20, 20L, duel.getFireworkColor());
        }

        me.robomonkey.versus.duel.playerdata.PlayerStats stats = StatsManager.getInstance().getStats(winner);
        if (stats != null) {
            try {
                me.robomonkey.versus.cosmetics.VictoryEffect eff = me.robomonkey.versus.cosmetics.VictoryEffect.valueOf(stats.getActiveVictoryEffect());
                eff.play(winner.getLocation());
            } catch (Exception ignored) {}
        }
    }

    private void renderLossEffects(Player loser, List<UUID> winners) {
        loser.sendMessage(Settings.getMessage(Setting.DUEL_LOSS_MESSAGE, Placeholder.of("%player%", PAPIUtil.getName(loser))));
        if (!winners.isEmpty()) {
            Player firstWinner = Bukkit.getPlayer(winners.get(0));
            if (firstWinner != null) {
                me.robomonkey.versus.duel.playerdata.PlayerStats stats = StatsManager.getInstance().getStats(firstWinner);
                if (stats != null) {
                    try {
                        me.robomonkey.versus.cosmetics.KillEffect eff = me.robomonkey.versus.cosmetics.KillEffect.valueOf(stats.getActiveKillEffect());
                        eff.play(loser.getLocation());
                    } catch (Exception ignored) {}
                }
            }
        }
    }

}
