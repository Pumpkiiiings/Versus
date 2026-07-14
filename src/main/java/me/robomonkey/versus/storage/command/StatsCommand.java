package me.robomonkey.versus.storage.command;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.storage.model.PlayerStats;
import me.robomonkey.versus.storage.manager.StatsManager;
import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;
import me.robomonkey.versus.config.model.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StatsCommand extends AbstractCommand {

    public StatsCommand() {
        super("stats", "versus.duel");
        setArgumentRequired(false);
        setMaxArguments(1);
        setUsage("/duel stats [player]");
        setPermissionRequired(false);
        setPlayersOnly(false);
        setDescription("Views a player's dueling stats.");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player target;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                error(sender, Settings.getMessage(Setting.ERROR_SPECIFY_PLAYER_STATS));
                return;
            }
            target = (Player) sender;
        } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                error(sender, Settings.getMessage(Setting.ERROR_PLAYER_NOT_FOUND));
                return;
            }
        }

        PlayerStats stats = StatsManager.getInstance().getStats(target);
        
        sender.sendMessage(Settings.getMessage(Setting.STATS_HEADER));
        sender.sendMessage(Settings.getMessage(Setting.STATS_TITLE, Placeholder.of("%player%", target.getName())));
        sender.sendMessage(Settings.getMessage(Setting.STATS_SPACER));
        sender.sendMessage(Settings.getMessage(Setting.STATS_WINS, Placeholder.of("%wins%", String.valueOf(stats.getWins()))));
        sender.sendMessage(Settings.getMessage(Setting.STATS_LOSSES, Placeholder.of("%losses%", String.valueOf(stats.getLosses()))));
        
        double ratio = stats.getLosses() == 0 ? stats.getWins() : (double) stats.getWins() / stats.getLosses();
        sender.sendMessage(Settings.getMessage(Setting.STATS_RATIO, Placeholder.of("%ratio%", String.format("%.2f", ratio))));
        sender.sendMessage(Settings.getMessage(Setting.STATS_SPACER));
        sender.sendMessage(Settings.getMessage(Setting.STATS_STREAK_CURRENT, Placeholder.of("%current_streak%", String.valueOf(stats.getCurrentStreak()))));
        sender.sendMessage(Settings.getMessage(Setting.STATS_STREAK_BEST, Placeholder.of("%best_streak%", String.valueOf(stats.getBestStreak()))));
        sender.sendMessage(Settings.getMessage(Setting.STATS_FOOTER));
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return List.of();
    }
}
