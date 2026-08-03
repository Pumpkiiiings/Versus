package me.robomonkey.versus.duel.manager;

import me.robomonkey.versus.betting.manager.BettingManager;
import me.robomonkey.versus.duel.model.Request;
import me.robomonkey.versus.party.manager.PartyManager;
import me.robomonkey.versus.party.model.Party;
import me.robomonkey.versus.kit.model.Kit;

import me.robomonkey.versus.arena.manager.ArenaManager;
import me.robomonkey.versus.dependency.PAPIUtil;
import me.robomonkey.versus.duel.manager.DuelManager;
import me.robomonkey.versus.config.model.Placeholder;
import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;
import me.robomonkey.versus.util.EffectUtil;
import me.robomonkey.versus.util.MessageUtil;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import me.robomonkey.versus.dependency.EconomyManager;
import me.robomonkey.versus.arena.model.Arena;

import java.util.*;

public class RequestManager {
    private ArrayList<Request> requestList = new ArrayList<>();
    private LinkedList<Request> queue = new LinkedList<>();
    private HashMap<UUID, Request> pendingConfirmations = new HashMap<>();
    private static RequestManager instance;

    public static RequestManager getInstance() {
        if (instance == null) {
            instance = new RequestManager();
        }
        return instance;
    }

    private Request getLatestRequest(Player player) {
        Optional<Request> optionalRequest = requestList.stream()
                .filter(request -> request.contains(player))
                .max(Comparator.comparingLong(Request::getTimeSent));
        return (optionalRequest.isPresent()) ? optionalRequest.get() : null;
    }

    public Request getRequest(Player requested, Player requesting) {
        Optional<Request> optionalRequest = requestList.stream()
                .filter(request -> request.matches(requested, requesting))
                .findFirst();
        return (optionalRequest.isPresent()) ? optionalRequest.get() : null;
    }

    private void updateQueue() {
        while (queue.peek() != null && !queue.peek().arePlayersOnline()) {
            queue.pop();
        }
        if (queue.size() > 0) {
            Request latest = queue.peek();
            
            Arena arena = null;
            if (latest.getRequestedArena() != null) {
                arena = ArenaManager.getInstance().getArena(latest.getRequestedArena());
            }
            if (arena == null) {
                if (latest.getRequestedKit() != null) {
                    arena = ArenaManager.getInstance().getAvailableArenaForKit(latest.getRequestedKit());
                } else {
                    arena = ArenaManager.getInstance().getAvailableArena();
                }
            }
            // If the specific arena they want is not available, we wait
            if (arena == null || !ArenaManager.getInstance().getAvailableArenas().contains(arena)) {
                return;
            }
            
            queue.pop();
            if (latest.isGroup()) {
                me.robomonkey.versus.party.model.Party p1 = me.robomonkey.versus.party.manager.PartyManager.getInstance().getParty(latest.getRequestedPlayer());
                me.robomonkey.versus.party.model.Party p2 = me.robomonkey.versus.party.manager.PartyManager.getInstance().getParty(latest.getRequestingPlayer());
                if (p1 != null && p2 != null) {
                    DuelManager.getInstance().setupGroupDuel(p1.getOnlinePlayers(), p2.getOnlinePlayers(), arena, latest.getBetAmount(), latest.getRequestedKit());
                }
            } else {
                if (latest.getBettingSession() != null) {
                    DuelManager.getInstance().registerDuel(java.util.List.of(latest.getRequestingPlayer()), java.util.List.of(latest.getRequestedPlayer()), latest.getBettingSession());
                } else {
                    DuelManager.getInstance().setupDuel(latest.getRequestedPlayer(), latest.getRequestingPlayer(), arena, latest.getBetAmount(), latest.getRequestedKit());
                }
            }
        }
    }

    public void removeRequest(Player requested, Player requesting) {
        Request requestToRemove = getRequest(requested, requesting);
        if (requestToRemove != null) requestList.remove(requestToRemove);
    }

    public void placeInQueue(Request request) {
        requestList.remove(request);
        queue.add(request);
        // Immediately try to process the queue — if shared arenas are available,
        // the duel should start right away without waiting for a duel to finish.
        updateQueue();
    }

    public Request getQueuedRequest(Player player) {
        Optional<Request> optionalRequest = queue.stream()
                .filter(request -> request.contains(player))
                .findFirst();
        return (optionalRequest.isPresent()) ? optionalRequest.get() : null;
    }

    public void cancelQueue(Player player) {
        queue.removeIf(request -> request.contains(player));
    }

    public boolean isQueued(Player player) {
        return queue.stream().anyMatch(request -> request.contains(player));
    }

    public Request getFirstInQueue() {
        if (queue.size() > 0) return queue.pop();
        else return null;
    }

    public void notifyDuelCompletion() {
        updateQueue();
    }

    public boolean hasIncomingRequest(Player requested) {
        return requestList.stream()
                .filter(request -> request.getRequested().equals(requested.getUniqueId()))
                .findFirst().isPresent();
    }

    public boolean hasOutgoingRequest(Player requesting) {
        return requestList.stream()
                .filter(request -> request.getRequesting().equals(requesting.getUniqueId()))
                .findFirst().isPresent();
    }

    public boolean anyPlayersQueued() {
        return queue.size() > 0;
    }

    public boolean contains(Player player) {
        return isQueued(player) || getLatestRequest(player) != null;
    }

    public void cancelRequest(Request request) {
        requestList.remove(request);
        queue.remove(request);
    }

    public UUID getRequested(Player requesting) {
        if (hasOutgoingRequest(requesting)) return getLatestRequest(requesting).getRequested();
        else return null;
    }

    public UUID getLatestRequester(Player requested) {
        if (hasIncomingRequest(requested)) return getLatestRequest(requested).getRequesting();
        else return null;
    }

    public boolean isRequestedBy(Player requesting, Player requested) {
        return requestList.stream().anyMatch(request -> requesting.equals(request.getRequestingPlayer()) &&
                requested.equals((request.getRequestedPlayer())));
    }


    public void queueConfirmation(Request request) {
        pendingConfirmations.put(request.getRequesting(), request);
    }

    public Request getPendingConfirmation(Player player) {
        return pendingConfirmations.get(player.getUniqueId());
    }

    public void removePendingConfirmation(Player player) {
        pendingConfirmations.remove(player.getUniqueId());
    }

    public void sendRequest(Player requesting, Player requested, double betAmount, String arenaName, me.robomonkey.versus.kit.model.Kit kit) {
        requestList.add(new Request(requested, requesting, betAmount, arenaName, kit));
        String sentRequestMessage = Settings.getMessage(Setting.SENT_REQUEST, new Placeholder("%player%", PAPIUtil.getName(requested)));
        String requestNotification = Settings.getMessage(Setting.REQUEST_NOTIFICATION, new Placeholder("%player%", PAPIUtil.getName(requesting)));
        
        if (betAmount > 0) {
            sentRequestMessage += " §7(Bet: §6" + EconomyManager.format(betAmount) + "§7)";
            requestNotification += " §7(Bet: §6" + EconomyManager.format(betAmount) + "§7)";
        }
        if (arenaName != null) {
            sentRequestMessage += " §7(Arena: §e" + arenaName + "§7)";
            requestNotification += " §7(Arena: §e" + arenaName + "§7)";
        }

        requesting.sendMessage(sentRequestMessage);
        TextComponent requestMessage = getRequestMessage(requested, requesting);
        requested.sendMessage(requestNotification);
        EffectUtil.playSound(requested, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        requested.spigot().sendMessage(requestMessage);
        sendRivalryNotice(requesting, requested);
    }

    /**
     * Shows both players their head-to-head record, so a challenge carries the weight of
     * the previous duels between them. Skipped when they have never faced each other.
     */
    private void sendRivalryNotice(Player requesting, Player requested) {
        me.robomonkey.versus.storage.manager.HistoryManager.getInstance()
                .getHeadToHead(requesting.getUniqueId(), requested.getUniqueId(), score -> {
                    if (score[0] == 0 && score[1] == 0) return;
                    if (requesting.isOnline()) {
                        requesting.sendMessage(Settings.getMessage(Setting.HISTORY_RIVALRY_NOTICE,
                                new Placeholder("%player%", PAPIUtil.getName(requested)),
                                new Placeholder("%wins%", String.valueOf(score[0])),
                                new Placeholder("%losses%", String.valueOf(score[1]))));
                    }
                    if (requested.isOnline()) {
                        requested.sendMessage(Settings.getMessage(Setting.HISTORY_RIVALRY_NOTICE,
                                new Placeholder("%player%", PAPIUtil.getName(requesting)),
                                new Placeholder("%wins%", String.valueOf(score[1])),
                                new Placeholder("%losses%", String.valueOf(score[0]))));
                    }
                });
    }

    public void acceptRequest(Player requested) throws PlayerOfflineException {
        Request currentRequest = getLatestRequest(requested);
        Player requester = currentRequest.getRequestingPlayer();
        if (requester == null) {
            throw new PlayerOfflineException();
        }
        handleAccept(requested, requester, currentRequest);
    }

    public void acceptSpecificRequest(Player requested, Player requester) throws PlayerOfflineException {
        Request currentRequest = getRequest(requested, requester);
        if (requester == null) {
            throw new PlayerOfflineException();
        }
        handleAccept(requested, requester, currentRequest);
    }
    
    private void handleAccept(Player requested, Player requester, Request currentRequest) {
        if (currentRequest.isGroup()) {
            requested.sendMessage(Settings.getMessage(Setting.ERROR_IS_GROUP_DUEL));
            return;
        }

        removeRequest(requested, requester);
        
        // Open Betting GUI instead of instantly starting
        me.robomonkey.versus.betting.manager.BettingManager.getInstance().startSession(requester, requested, currentRequest.getRequestedArena(), currentRequest.getRequestedKit());
    }

    public void denyRequest(Player requested, Player requester) {
        removeRequest(requested, requester);
        if (requester != null) {
            requester.sendMessage(Settings.getMessage(Setting.DENIED_REQUEST, Placeholder.of("%player%", PAPIUtil.getName(requested))));
            requested.sendMessage(Settings.getMessage(Setting.DENIED_REQUEST_CONFIRMATION, Placeholder.of("%player%", PAPIUtil.getName(requester))));
        }
    }

    public class PlayerOfflineException extends Exception {
        public PlayerOfflineException() {
            super("The requested player is offline.");
        }
    }

    private TextComponent getRequestMessage(Player requested, Player requesting) {
        String acceptCommand = "/duel " + requesting.getName();
        String denyCommand = "/duel deny " + requesting.getName();
        String acceptButtonText = Settings.getMessage(Setting.ACCEPT_BUTTON);
        String denyButtonText = Settings.getMessage(Setting.DENY_BUTTON);
        TextComponent parentButton = MessageUtil.createButton(acceptButtonText, acceptCommand, acceptCommand);
        TextComponent denyButton = MessageUtil.createButton(denyButtonText, denyCommand, denyCommand);
        parentButton.addExtra("  ");
        parentButton.addExtra(denyButton);
        return parentButton;
    }
}
