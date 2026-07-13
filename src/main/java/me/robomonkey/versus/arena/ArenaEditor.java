package me.robomonkey.versus.arena;

import me.robomonkey.versus.kit.Kit;
import me.robomonkey.versus.kit.KitSelectionGUI;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import me.robomonkey.versus.settings.Placeholder;
import me.robomonkey.versus.util.MessageUtil;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ArenaEditor {

    static void displayInstructionalMessage(Arena targetArena, ArenaProperty property, Player player) {
        String buttonBase = MessageUtil.color(Settings.getMessage(Setting.ARENA_EDIT_SELECT_PROPERTY));
        String explanationBase = MessageUtil.color("&h " + property.toFriendlyString());
        String explanationOnHover = MessageUtil.color("&s" + property.getExplanation());
        String commandOnClick = "/arena set " + targetArena.getName() + " " + property.toString();
        String commandOnHover = MessageUtil.color("&s" + commandOnClick);
        TextComponent setPropertyMessage = MessageUtil.getClickableMessage(buttonBase, commandOnClick, commandOnHover, "&boldCLICK HERE");
        TextComponent explanationMessage = MessageUtil.getHoverText(explanationBase, explanationOnHover);
        setPropertyMessage.addExtra(explanationMessage);
        player.spigot().sendMessage(setPropertyMessage);
    }

    public static void openEditingMenu(Player player, Arena targetArena) {
        String message = Settings.getMessage(Setting.ARENA_EDIT_TITLE, Placeholder.of("%arena%", targetArena.getName()));
        String visitCommand = "/arena visit " + targetArena.getName();
        String hoverText = MessageUtil.color(Settings.getMessage(Setting.ARENA_EDIT_VISIT_HOVER, Placeholder.of("%arena%", targetArena.getName())));
        TextComponent messageReplaced = MessageUtil.getClickableMessage(message, visitCommand, hoverText, MessageUtil.get("&boldVISIT"));
        player.spigot().sendMessage(messageReplaced);
        Arrays.stream(ArenaProperty.values()).forEach((property) -> displayInstructionalMessage(targetArena, property, player));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
    }

    public static void changeArenaProperty(Arena targetArena, ArenaProperty property, Player player, Runnable after) {
        if (property == ArenaProperty.KIT) {
            ArenaKitsGUI kitGUI = new ArenaKitsGUI(player, targetArena, () -> {
                player.sendMessage(Settings.getMessage(Setting.ARENA_KIT_SET, 
                        Placeholder.of("%kit%", "Múltiples"), Placeholder.of("%arena%", targetArena.getName())));
                after.run();
            });
            kitGUI.open();
            return;
        }
        if (property == ArenaProperty.SHARED) {
            targetArena.setShared(!targetArena.isShared());
            String state = targetArena.isShared() ? "enabled" : "disabled";
            player.sendMessage(Settings.getMessage(Setting.ARENA_SHARED_MODE_TOGGLED, 
                    Placeholder.of("%state%", state), Placeholder.of("%arena%", targetArena.getName())));
            after.run();
            return;
        }
        targetArena.setLocationProperty(property, player.getLocation());
        player.sendMessage(Settings.getMessage(Setting.ARENA_PROPERTY_SET, 
                Placeholder.of("%property%", property.toFriendlyString()), Placeholder.of("%arena%", targetArena.getName())));
        after.run();
    }

    public static void changeArenaProperty(Arena targetArena, ArenaProperty property, Player player) {
        if (property == ArenaProperty.KIT) {
            ArenaKitsGUI kitGUI = new ArenaKitsGUI(player, targetArena, () -> {
                player.sendMessage(Settings.getMessage(Setting.ARENA_KIT_SET, 
                        Placeholder.of("%kit%", "Múltiples"), Placeholder.of("%arena%", targetArena.getName())));
            });
            kitGUI.open();
            return;
        }
        if (property == ArenaProperty.SHARED) {
            targetArena.setShared(!targetArena.isShared());
            String state = targetArena.isShared() ? "enabled" : "disabled";
            player.sendMessage(Settings.getMessage(Setting.ARENA_SHARED_MODE_TOGGLED, 
                    Placeholder.of("%state%", state), Placeholder.of("%arena%", targetArena.getName())));
            return;
        }
        targetArena.setLocationProperty(property, player.getLocation());
        player.sendMessage(Settings.getMessage(Setting.ARENA_PROPERTY_SET, 
                Placeholder.of("%property%", property.toFriendlyString()), Placeholder.of("%arena%", targetArena.getName())));
    }

    public static void changeKit(Arena arena, Player player, Kit kit) {
        arena.addKit(kit);
        player.sendMessage(Settings.getMessage(Setting.ARENA_KIT_SET, 
                Placeholder.of("%kit%", kit.getName()), Placeholder.of("%arena%", arena.getName())));
    }
}
