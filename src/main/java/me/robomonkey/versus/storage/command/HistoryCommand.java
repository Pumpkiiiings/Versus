package me.robomonkey.versus.storage.command;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.config.model.Placeholder;
import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;
import me.robomonkey.versus.storage.manager.HistoryManager;
import me.robomonkey.versus.storage.model.DuelRecord;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class HistoryCommand extends AbstractCommand {

    private static final int ENTRY_LIMIT = 10;

    public HistoryCommand() {
        super("history", "versus.duel");
        setArgumentRequired(false);
        setMaxArguments(1);
        setUsage("/duel history [player]");
        setPermissionRequired(false);
        setPlayersOnly(false);
        setDescription("Views a player's recent duels.");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        UUID targetUuid;
        String targetName;

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                error(sender, Settings.getMessage(Setting.ERROR_SPECIFY_PLAYER_STATS));
                return;
            }
            Player self = (Player) sender;
            targetUuid = self.getUniqueId();
            targetName = self.getName();
        } else {
            OfflinePlayer target = resolve(args[0]);
            if (target == null) {
                error(sender, Settings.getMessage(Setting.ERROR_PLAYER_NOT_FOUND));
                return;
            }
            targetUuid = target.getUniqueId();
            targetName = target.getName() != null ? target.getName() : args[0];
        }

        HistoryManager.getInstance().getRecent(targetUuid, ENTRY_LIMIT,
                records -> render(sender, targetUuid, targetName, records));
    }

    private void render(CommandSender sender, UUID targetUuid, String targetName, List<DuelRecord> records) {
        sender.sendMessage(Settings.getMessage(Setting.HISTORY_HEADER, Placeholder.of("%player%", targetName)));

        if (records.isEmpty()) {
            sender.sendMessage(Settings.getMessage(Setting.HISTORY_EMPTY));
            return;
        }

        SimpleDateFormat format = dateFormat();
        for (DuelRecord record : records) {
            Setting line = record.isWinner(targetUuid) ? Setting.HISTORY_ENTRY_WIN : Setting.HISTORY_ENTRY_LOSS;
            sender.sendMessage(Settings.getMessage(line,
                    Placeholder.of("%opponent%", record.getOpponentName(targetUuid)),
                    Placeholder.of("%kit%", record.getKitName() != null ? record.getKitName() : "?"),
                    Placeholder.of("%date%", format.format(new Date(record.getTimestamp())))));
        }
    }

    private SimpleDateFormat dateFormat() {
        try {
            return new SimpleDateFormat(Settings.getMessage(Setting.HISTORY_DATE_FORMAT));
        } catch (IllegalArgumentException e) {
            return new SimpleDateFormat("dd/MM HH:mm");
        }
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
