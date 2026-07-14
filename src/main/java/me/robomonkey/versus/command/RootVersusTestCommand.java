package me.robomonkey.versus.command;

import me.robomonkey.versus.cosmetic.manager.CosmeticsManager;
import me.robomonkey.versus.cosmetic.model.KillEffect;
import me.robomonkey.versus.cosmetic.model.VictoryEffect;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RootVersusTestCommand extends RootCommand {

    public RootVersusTestCommand() {
        super("versustest", "versus.admin");
        setPermissionRequired(true);
        setPlayersOnly(true);
        setMinArguments(2);
        setMaxArguments(2);
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        // /versustest <win/kill> <effect>
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /versustest <win/kill> <effect_id>");
            return;
        }

        Player player = (Player) sender;
        String type = args[0].toLowerCase();
        String effectId = args[1];

        if (type.equals("win") || type.equals("victory")) {
            VictoryEffect effect = CosmeticsManager.getInstance().getVictoryEffect(effectId);
            if (effect == null) {
                sender.sendMessage("§cVictory effect '" + effectId + "' not found.");
                return;
            }
            effect.play(player.getLocation());
            sender.sendMessage("§aPlaying victory effect: " + effect.getDisplayName());
        } else if (type.equals("kill") || type.equals("death")) {
            KillEffect effect = CosmeticsManager.getInstance().getKillEffect(effectId);
            if (effect == null) {
                sender.sendMessage("§cKill effect '" + effectId + "' not found.");
                return;
            }
            effect.play(player.getLocation());
            sender.sendMessage("§aPlaying kill effect: " + effect.getDisplayName());
        } else {
            sender.sendMessage("§cUsage: /versustest <win/kill> <effect_id>");
        }
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("win");
            completions.add("kill");
        } else if (args.length == 2) {
            String type = args[0].toLowerCase();
            if (type.equals("win") || type.equals("victory")) {
                completions.addAll(CosmeticsManager.getInstance().getVictoryEffects().stream().map(e -> e.getId()).collect(Collectors.toList()));
            } else if (type.equals("kill") || type.equals("death")) {
                completions.addAll(CosmeticsManager.getInstance().getKillEffects().stream().map(e -> e.getId()).collect(Collectors.toList()));
            }
        }
        return completions;
    }
}
