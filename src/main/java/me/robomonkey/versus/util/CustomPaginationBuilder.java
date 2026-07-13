package me.robomonkey.versus.util;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import com.samjakob.spigui.toolbar.SGToolbarBuilder;
import com.samjakob.spigui.toolbar.SGToolbarButtonType;
import me.robomonkey.versus.settings.Placeholder;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import org.bukkit.Material;
import org.bukkit.event.Event;

public class CustomPaginationBuilder implements SGToolbarBuilder {

    @Override
    public SGButton buildToolbarButton(int slot, int page, SGToolbarButtonType type, SGMenu menu) {
        switch (type) {
            case PREV_BUTTON:
                if (menu.getCurrentPage() > 0) {
                    String name = Settings.getMessage(Setting.PAGINATION_PREVIOUS_PAGE);
                    String lore = Settings.getMessage(Setting.PAGINATION_PREVIOUS_LORE, Placeholder.of("%page%", String.valueOf(menu.getCurrentPage())));
                    return new SGButton(new ItemBuilder(Material.ARROW)
                            .name(name)
                            .lore(lore.split("\n"))
                            .build()
                    ).withListener(event -> {
                        event.setResult(Event.Result.DENY);
                        menu.previousPage(event.getWhoClicked());
                    });
                } else return null;

            case CURRENT_BUTTON:
                String name = Settings.getMessage(Setting.PAGINATION_CURRENT_PAGE, 
                    Placeholder.of("%page%", String.valueOf(menu.getCurrentPage() + 1)),
                    Placeholder.of("%max%", String.valueOf(menu.getMaxPage())));
                String lore = Settings.getMessage(Setting.PAGINATION_CURRENT_LORE, 
                    Placeholder.of("%page%", String.valueOf(menu.getCurrentPage() + 1)));
                return new SGButton(new ItemBuilder(Material.NAME_TAG)
                        .name(name)
                        .lore(lore.split("\n"))
                        .build()
                ).withListener(event -> event.setResult(Event.Result.DENY));

            case NEXT_BUTTON:
                if (menu.getCurrentPage() < menu.getMaxPage() - 1) {
                    String nextName = Settings.getMessage(Setting.PAGINATION_NEXT_PAGE);
                    String nextLore = Settings.getMessage(Setting.PAGINATION_NEXT_LORE, 
                        Placeholder.of("%page%", String.valueOf(menu.getCurrentPage() + 2)));
                    return new SGButton(new ItemBuilder(Material.ARROW)
                            .name(nextName)
                            .lore(nextLore.split("\n"))
                            .build()
                    ).withListener(event -> {
                        event.setResult(Event.Result.DENY);
                        menu.nextPage(event.getWhoClicked());
                    });
                } else return null;

            case UNASSIGNED:
            default:
                return null;
        }
    }
}
