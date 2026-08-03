package me.robomonkey.versus.storage.command;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.config.model.Placeholder;
import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;
import me.robomonkey.versus.storage.manager.HistoryManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * /duel vs &lt;player&gt; — shows the head-to-head record against another player.
 */
public class VersusCommand extends AbstractCommand {

    public VersusCommand() {
        super("vs", "versus.duel");
        setArgumentRequired(true);
        setMinArguments(1);
        setMaxArguments(1);
        setUsage("/duel vs <player>");
        setPermissionRequired(false);
        setPlayersOnly(true);
        setDescription("Shows your head-to-head record against a player.");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player self = (Player) sender;

        OfflinePlayer target = resolve(args[0]);
        if (target == null) {
            error(sender, Settings.getMessage(Setting.ERROR_PLAYER_NOT_FOUND));
            return;
        }
        if (target.getUniqueId().equals(self.getUniqueId())) {
            error(sender, Settings.getMessage(Setting.ERROR_CANNOT_DUEL_SELF));
            return;
        }

        String targetName = target.getName() != null ? target.getName() : args[0];
        UUID targetUuid = target.getUniqueId();

        HistoryManager.getInstance().getHeadToHead(self.getUniqueId(), targetUuid,
                score -> render(sender, targetName, score[0], score[1]));
    }

    private void render(CommandSender sender, String targetName, int wins, int losses) {
        sender.sendMessage(Settings.getMessage(Setting.HISTORY_VS_HEADER, Placeholder.of("%player%", targetName)));

        if (wins == 0 && losses == 0) {
            sender.sendMessage(Settings.getMessage(Setting.HISTORY_VS_EMPTY));
            return;
        }

        sender.sendMessage(Settings.getMessage(Setting.HISTORY_VS_SCORE,
                Placeholder.of("%player%", targetName),
                Placeholder.of("%wins%", String.valueOf(wins)),
                Placeholder.of("%losses%", String.valueOf(losses))));

        Setting verdict;
        if (wins > losses) verdict = Setting.HISTORY_VS_LEADING;
        else if (wins < losses) verdict = Setting.HISTORY_VS_TRAILING;
        else verdict = Setting.HISTORY_VS_TIED;
        sender.sendMessage(Settings.getMessage(verdict));
    }

    @SuppressWarnings("deprecation")
    private OfflinePlayer resolve(String name) {
        Player online = Bukkit.getPlayer(name);
        if (online != null) return online;
        OfflinePlayer offline = Bukkit.getOfflinePlayer(name);
        return offline.hasPlayedBefore() ? offline : null;
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return List.of();
    }
}
