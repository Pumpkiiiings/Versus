package me.robomonkey.versus.queue;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class QueueTask extends BukkitRunnable {
    private final QueueManager queueManager;

    public QueueTask(QueueManager queueManager) {
        this.queueManager = queueManager;
    }

    @Override
    public void run() {
        List<QueueEntry> queue = queueManager.getQueue();
        if (queue.size() < 2) return;

        List<QueueEntry> matched = new ArrayList<>();

        for (int i = 0; i < queue.size(); i++) {
            QueueEntry entry1 = queue.get(i);
            if (matched.contains(entry1)) continue;

            for (int j = i + 1; j < queue.size(); j++) {
                QueueEntry entry2 = queue.get(j);
                if (matched.contains(entry2)) continue;

                if (!entry1.getKit().equals(entry2.getKit())) continue;

                int eloDiff = Math.abs(entry1.getElo() - entry2.getElo());
                int baseDiff = me.robomonkey.versus.config.model.Settings.getNumber(me.robomonkey.versus.config.model.Setting.RANKED_QUEUE_BASE_ELO_DIFF);
                int diffPerSec = me.robomonkey.versus.config.model.Settings.getNumber(me.robomonkey.versus.config.model.Setting.RANKED_QUEUE_ELO_DIFF_PER_SECOND);
                int allowedDiff1 = baseDiff + (entry1.getSecondsInQueue() * diffPerSec);
                int allowedDiff2 = baseDiff + (entry2.getSecondsInQueue() * diffPerSec);

                if (eloDiff <= Math.max(allowedDiff1, allowedDiff2)) {
                    // Match found!
                    matched.add(entry1);
                    matched.add(entry2);
                    queueManager.createRankedMatch(entry1, entry2);
                    break;
                }
            }
        }

        queue.removeAll(matched);
    }
}
