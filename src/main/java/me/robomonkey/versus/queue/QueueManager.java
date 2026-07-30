package me.robomonkey.versus.queue;

import me.robomonkey.versus.Versus;
import me.robomonkey.versus.arena.manager.ArenaManager;
import me.robomonkey.versus.arena.model.Arena;
import me.robomonkey.versus.duel.manager.DuelManager;
import me.robomonkey.versus.duel.model.Duel;
import me.robomonkey.versus.kit.model.Kit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QueueManager {
    private static QueueManager instance;
    private final List<QueueEntry> queue = new ArrayList<>();
    private QueueTask queueTask;

    private QueueManager() {
        this.queueTask = new QueueTask(this);
        this.queueTask.runTaskTimer(Versus.getInstance(), 20L, 20L); // run every second
    }

    public static QueueManager getInstance() {
        if (instance == null) {
            instance = new QueueManager();
        }
        return instance;
    }

    public void addPlayer(Player player, Kit kit, int elo) {
        if (isInQueue(player)) return;
        if (DuelManager.getInstance().isDueling(player)) {
            player.sendMessage(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.ERROR_ALREADY_QUEUEING));
            return;
        }
        queue.add(new QueueEntry(player, kit, elo));
        
        me.robomonkey.versus.config.model.Placeholder kitPl = new me.robomonkey.versus.config.model.Placeholder("%kit%", kit.getName());
        me.robomonkey.versus.config.model.Placeholder eloPl = new me.robomonkey.versus.config.model.Placeholder("%elo%", String.valueOf(elo));
        player.sendMessage(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.RANKED_QUEUE_JOIN, kitPl, eloPl));
    }

    public void removePlayer(Player player) {
        Iterator<QueueEntry> iter = queue.iterator();
        while (iter.hasNext()) {
            QueueEntry entry = iter.next();
            if (entry.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                iter.remove();
                player.sendMessage(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.RANKED_QUEUE_LEAVE));
                break;
            }
        }
    }

    public boolean isInQueue(Player player) {
        return queue.stream().anyMatch(entry -> entry.getPlayer().getUniqueId().equals(player.getUniqueId()));
    }

    public List<QueueEntry> getQueue() {
        return queue;
    }

    public void createRankedMatch(QueueEntry entry1, QueueEntry entry2) {
        Player p1 = entry1.getPlayer();
        Player p2 = entry2.getPlayer();

        if (p1 == null || !p1.isOnline() || p2 == null || !p2.isOnline()) return;

        Arena arena = ArenaManager.getInstance().getAvailableArenaForKit(entry1.getKit());
        if (arena == null) {
            arena = ArenaManager.getInstance().getAvailableArena();
        }
        
        if (arena == null) {
            p1.sendMessage(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.NO_ARENAS_AVAILABLE));
            p2.sendMessage(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.NO_ARENAS_AVAILABLE));
            return;
        }

        p1.sendMessage(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.RANKED_QUEUE_MATCH_FOUND));
        p2.sendMessage(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.RANKED_QUEUE_MATCH_FOUND));

        // Set up the duel directly
        Duel duel = new Duel(arena, List.of(p1), List.of(p2), entry1.getKit());
        duel.setRanked(true);
        DuelManager.getInstance().addDuel(duel);
        
        // This simulates what setupGroupDuel does internally
        try {
            java.lang.reflect.Method setupMethod = DuelManager.class.getDeclaredMethod("executeDuelSetup", Duel.class, List.class, List.class);
            setupMethod.setAccessible(true);
            setupMethod.invoke(DuelManager.getInstance(), duel, List.of(p1), List.of(p2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
