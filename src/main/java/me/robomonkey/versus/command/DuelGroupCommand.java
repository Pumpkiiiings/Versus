package me.robomonkey.versus.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DuelGroupCommand extends RootCommand {

    public DuelGroupCommand() {
        super("duelgroup", "versus.duel");
        this.addBranches(
                new DuelGroupCreate(),
                new DuelGroupInvite(),
                new DuelGroupAccept(),
                new DuelGroupDecline(),
                new DuelGroupDisband(),
                new DuelGroupLeave()
        );
        setArgumentRequired(false);
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(me.robomonkey.versus.util.MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.DUELGROUP_HELP_HEADER)));
            sender.sendMessage(me.robomonkey.versus.util.MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.DUELGROUP_HELP_CREATE)));
            sender.sendMessage(me.robomonkey.versus.util.MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.DUELGROUP_HELP_INVITE)));
            sender.sendMessage(me.robomonkey.versus.util.MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.DUELGROUP_HELP_ACCEPT)));
            sender.sendMessage(me.robomonkey.versus.util.MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.DUELGROUP_HELP_DECLINE)));
            sender.sendMessage(me.robomonkey.versus.util.MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.DUELGROUP_HELP_LEAVE)));
            sender.sendMessage(me.robomonkey.versus.util.MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.DUELGROUP_HELP_DISBAND)));
            sender.sendMessage(me.robomonkey.versus.util.MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.DUELGROUP_HELP_DUEL)));
            sender.sendMessage(me.robomonkey.versus.util.MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.DUELGROUP_HELP_ACCEPTDUEL)));
            sender.sendMessage(me.robomonkey.versus.util.MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.DUELGROUP_HELP_DECLINEDUEL)));
        }
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}
