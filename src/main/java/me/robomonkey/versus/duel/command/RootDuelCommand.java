package me.robomonkey.versus.duel.command;

import me.robomonkey.versus.command.RootCommand;
import me.robomonkey.versus.duel.DuelManager;
import me.robomonkey.versus.duel.request.RequestManager;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;
import me.robomonkey.versus.dependency.EconomyManager;
import me.robomonkey.versus.arena.ArenaManager;
import me.robomonkey.versus.arena.ArenaSelectionGUI;
import me.robomonkey.versus.kit.KitSelectionGUI;
import me.robomonkey.versus.arena.Arena;

public class RootDuelCommand extends RootCommand {

    public RootDuelCommand() {
        super("duel", "versus.duel");
        setPermissionRequired(Settings.is(Setting.PERMISSION_REQUIRED_TO_DUEL));
        setPlayersOnly(true);
        setArgumentRequired(true);
        setUsage("/duel <player> [bet] [arena]");
        setDescription("Sends a duel request.");
        addBranches(new DenyCommand(),
                new CancelCommand(),
                new AcceptCommand(),
                new ConfirmCommand(),
                new StatsCommand(),
                new LeaveCommand(),
                new RewardsCommand());
        setAutonomous(true);
        enforcePermissionRulesForChildren();
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        RequestManager requestManager = RequestManager.getInstance();
        Player player = (Player) sender;
        String playerNameRequested = args[0];
        Player requested = Bukkit.getPlayer(playerNameRequested);
        if (requested == null) {
            error(sender, Settings.getMessage(Setting.ERROR_PLAYER_OFFLINE, new me.robomonkey.versus.settings.Placeholder("%player%", playerNameRequested)));
            return;
        }
        if (requested.equals(player)) {
            error(sender, Settings.getMessage(Setting.ERROR_CANNOT_DUEL_SELF));
            return;
        }
        if (DuelManager.getInstance().isDueling(player)) {
            error(sender, Settings.getMessage(Setting.ERROR_CANNOT_DUEL_RIGHT_NOW));
            return;
        }
        if (DuelManager.getInstance().isDueling(requested) || requestManager.isQueued(requested)) {
            error(sender, Settings.getMessage(Setting.ERROR_TARGET_CANNOT_DUEL, new me.robomonkey.versus.settings.Placeholder("%player%", requested.getName())));
            return;
        }
        if (requestManager.hasIncomingRequest(player)
                && requestManager.isRequestedBy(requested, player)) {
            try {
                RequestManager.getInstance().acceptSpecificRequest(player, requested);
            } catch (RequestManager.PlayerOfflineException e) {
                error(player, Settings.getMessage(Setting.ERROR_REQUESTER_OFFLINE));
            }
            return;
        }
        if (requestManager.isQueued(player)) {
            error(sender, Settings.getMessage(Setting.ERROR_ALREADY_QUEUEING));
            return;
        }
        if (requestManager.isRequestedBy(player, requested)) {
            error(sender, Settings.getMessage(Setting.ERROR_WAIT_FOR_RESPONSE, new me.robomonkey.versus.settings.Placeholder("%player%", requested.getName())));
            return;
        }
        
        String arenaName = null;
        if (args.length > 1) {
            arenaName = args[1];
            Arena arena = ArenaManager.getInstance().getArena(arenaName);
            if (arena == null) {
                error(sender, Settings.getMessage(Setting.ERROR_ARENA_NOT_EXIST, new me.robomonkey.versus.settings.Placeholder("%arena%", arenaName)));
                return;
            }
            
            // If arena is specified, open Kit Selection directly filtered to this arena's kits
            KitSelectionGUI kitGUI = new KitSelectionGUI(player, arena.getKits(), (kit, whoClicked) -> {
                requestManager.sendRequest(player, requested, 0.0, arena.getName(), kit);
            });
            kitGUI.open();
        } else {
            // Open Arena Selection GUI
            ArenaSelectionGUI arenaGUI = new ArenaSelectionGUI(player, selectedArena -> {
                List<me.robomonkey.versus.kit.Kit> allowedKits = selectedArena != null ? selectedArena.getKits() : null;
                String finalArenaName = selectedArena != null ? selectedArena.getName() : null;
                
                // Then open Kit Selection GUI
                KitSelectionGUI kitGUI = new KitSelectionGUI(player, allowedKits, (kit, whoClicked) -> {
                    requestManager.sendRequest(player, requested, 0.0, finalArenaName, kit);
                });
                kitGUI.open();
            });
            arenaGUI.open();
        }
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return Bukkit.getOnlinePlayers()
                .stream()
                .map(player -> player.getName())
                .collect(Collectors.toList());
    }
}
