package me.robomonkey.versus.command;

import me.robomonkey.versus.storage.manager.StatsManager;
import me.robomonkey.versus.storage.model.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RootVersusStatsCommand extends RootCommand {

    public RootVersusStatsCommand() {
        super("versustats", "versus.admin");
        setPermissionRequired(true);
        setPlayersOnly(false);
        setMinArguments(3);
        setMaxArguments(4);
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        // /versustats <give/set> <player> <wins/losses/streak> <amount>
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /versustats <give/set> <player> <wins/losses/streak> <amount>");
            return;
        }

        String action = args[0].toLowerCase();
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return;
        }

        String stat = args[2].toLowerCase();
        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cAmount must be a number.");
            return;
        }

        PlayerStats stats = StatsManager.getInstance().getStats(target);
        if (stats == null) {
            sender.sendMessage("§cCould not load stats for " + target.getName());
            return;
        }

        if (action.equals("give") || action.equals("add")) {
            switch (stat) {
                case "wins":
                case "win":
                case "w":
                    stats.setWins(stats.getWins() + amount);
                    break;
                case "losses":
                case "loss":
                case "l":
                    stats.setLosses(stats.getLosses() + amount);
                    break;
                case "streak":
                    for (int i = 0; i < amount; i++) stats.addStreak();
                    break;
                default:
                    sender.sendMessage("§cInvalid stat. Use wins, losses, or streak.");
                    return;
            }
        } else if (action.equals("set")) {
            switch (stat) {
                case "wins":
                case "win":
                case "w":
                    stats.setWins(amount);
                    break;
                case "losses":
                case "loss":
                case "l":
                    stats.setLosses(amount);
                    break;
                case "streak":
                    stats.resetStreak();
                    for (int i = 0; i < amount; i++) stats.addStreak();
                    break;
                default:
                    sender.sendMessage("§cInvalid stat. Use wins, losses, or streak.");
                    return;
            }
        } else {
            sender.sendMessage("§cUsage: /versustats <give/set> <player> <wins/losses/streak> <amount>");
            return;
        }

        StatsManager.getInstance().savePlayer(stats);
        sender.sendMessage("§aSuccessfully updated " + target.getName() + "'s " + stat + "!");
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("give");
            completions.add("set");
        } else if (args.length == 2) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                completions.add(p.getName());
            }
        } else if (args.length == 3) {
            completions.add("wins");
            completions.add("losses");
            completions.add("streak");
        }
        return completions;
    }
}
