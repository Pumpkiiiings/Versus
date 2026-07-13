package me.robomonkey.versus.duel;

import me.robomonkey.versus.arena.Arena;
import me.robomonkey.versus.settings.Placeholder;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import me.robomonkey.versus.util.EffectUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Duel {
    private final ArrayList<Player> players = new ArrayList<>();
    private List<Player> team1 = new ArrayList<>();
    private List<Player> team2 = new ArrayList<>();
    private final Arena activeArena;
    private DuelState state = DuelState.IDLE;
    private List<UUID> winners = new ArrayList<>();
    private List<UUID> losers = new ArrayList<>();
    private boolean isPublic = Settings.is(Setting.ANNOUNCE_DUELS);
    private Countdown countdown = null;
    private boolean fightMusicEnabled = Settings.is(Setting.FIGHT_MUSIC_ENABLED);
    private boolean victoryMusicEnabled = Settings.is(Setting.VICTORY_MUSIC_ENABLED);
    private boolean victoryEffectsEnabled = Settings.is(Setting.VICTORY_EFFECTS_ENABLED);
    private boolean fireworksEnabled = Settings.is(Setting.FIREWORKS_ENABLED);
    private Color fireworkColor = Settings.getColor(Setting.FIREWORKS_COLOR);
    private Sound victorySong = Settings.getSong(Setting.VICTORY_MUSIC);
    private Sound fightMusic = Settings.getSong(Setting.FIGHT_MUSIC);
    private boolean blindnessEnabled = Settings.is(Setting.BLINDNESS_EFFECTS_ENABLED);
    private double betAmount = 0.0;
    private me.robomonkey.versus.duel.betting.BettingSession bettingSession = null;
    private me.robomonkey.versus.kit.Kit kit;

    public Duel(Arena arena, List<Player> team1, List<Player> team2, me.robomonkey.versus.kit.Kit kit) {
        this.team1.addAll(team1);
        this.team2.addAll(team2);
        this.players.addAll(team1);
        this.players.addAll(team2);
        this.activeArena = arena;
        this.kit = kit;
    }

    public me.robomonkey.versus.kit.Kit getKit() {
        return kit;
    }

    public List<Player> getTeam1() {
        return team1;
    }

    public List<Player> getTeam2() {
        return team2;
    }

    public ArrayList<Player> getPlayers() {
        return this.players;
    }

    public Arena getArena() {
        return this.activeArena;
    }

    public void setBetAmount(double betAmount) {
        this.betAmount = betAmount;
    }
    
    public me.robomonkey.versus.duel.betting.BettingSession getBettingSession() {
        return bettingSession;
    }
    
    public void setBettingSession(me.robomonkey.versus.duel.betting.BettingSession bettingSession) {
        this.bettingSession = bettingSession;
    }

    public double getBetAmount() {
        return this.betAmount;
    }

    public Countdown getCountdown() {
        return this.countdown;
    }

    public DuelState getState() {
        return state;
    }

    public boolean isActive() {
        return (state == DuelState.ACTIVE || state == DuelState.COUNTDOWN);
    }

    public boolean isFireworksEnabled() {
        return fireworksEnabled;
    }

    public boolean isVictoryEffectsEnabled() {
        return victoryEffectsEnabled;
    }

    public List<UUID> getWinners() {
        return winners;
    }

    public List<UUID> getLosers() {
        return losers;
    }

    public boolean isPublic() {
        return this.isPublic;
    }

    public Sound getFightMusic() {
        return fightMusic;
    }

    public Sound getVictorySong() {
        return victorySong;
    }

    public boolean isFightMusicEnabled() {
        return this.fightMusicEnabled;
    }

    public boolean isVictoryMusicEnabled() {
        return this.victoryMusicEnabled;
    }

    public Color getFireworkColor() {
        return (fireworkColor == null) ? Color.ORANGE : fireworkColor;
    }

    public void setWinners(List<Player> winners) {
        this.winners.clear();
        for (Player p : winners) {
            this.winners.add(p.getUniqueId());
        }
    }

    public void setLosers(List<Player> losers) {
        this.losers.clear();
        for (Player p : losers) {
            this.losers.add(p.getUniqueId());
        }
    }

    public void setState(DuelState state) {
        this.state = state;
    }

    public void end(List<Player> winners, List<Player> losers) {
        setWinners(winners);
        setLosers(losers);
        this.setState(DuelState.ENDED);
    }

    public void startCountdown(Runnable onCountdownExpiration) {
        setState(DuelState.COUNTDOWN);
        int countdownDuration = Settings.getNumber(Setting.COUNTDOWN_DURATION);
        players.stream().forEach((player) -> EffectUtil.freezePlayer(player));
        countdown = new Countdown(countdownDuration, () -> {
            players.stream()
                    .forEach(EffectUtil::unfreezePlayer);
            onCountdownExpiration.run();
        });
        countdown.setOnCount(() -> {
            String countdownMessage = Settings.getMessage(Setting.COUNTDOWN_MESSAGE, Placeholder.of("%seconds%", countdown.getSecondsRemaining()));
            String countdownTitle = Settings.getMessage(Setting.COUNTDOWN_TITLE, Placeholder.of("%seconds%", countdown.getSecondsRemaining()));
            players.forEach(player -> {
                EffectUtil.playSound(player, Sound.UI_BUTTON_CLICK);
                EffectUtil.sendTitle(player, countdownTitle, 30, false);
                player.sendMessage(countdownMessage);
            });
        });
        countdown.initiateCountdown();
    }

    public void cancelCountdown() {
        countdown.cancel();
    }

    public void spectate(Player player) {
        String spectateMessage = Settings.getMessage(
                Setting.DUEL_SPECTATE_MESSAGE,
                Placeholder.of("%player_one%", getPlayers().get(0).getName()),
                Placeholder.of("%player_two%", getPlayers().get(1).getName()));
        player.sendMessage(spectateMessage);
        me.robomonkey.versus.duel.DuelManager.getInstance().addSpectator(player, this);
    }

    public void removeFromSpectating(Player player) {
        // Obsolete as DuelManager now handles returning spectators when duel ends,
        // but if someone manually calls this, we just TP them to spawn as a fallback
        player.teleport(player.getWorld().getSpawnLocation());
    }
}
