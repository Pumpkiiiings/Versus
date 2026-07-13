package me.robomonkey.versus.duel.eventlisteners;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntitySoundEffect;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityStatus;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import me.robomonkey.versus.arena.ArenaVisibilityManager;
import me.robomonkey.versus.duel.Duel;
import me.robomonkey.versus.duel.DuelManager;
import org.bukkit.entity.Player;

public class PacketVisibilityListener extends PacketListenerAbstract {

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getUser() != null) {
            Player receiver = (Player) event.getPlayer();
            if (receiver == null) return;

            int entityId = -1;

            if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
                WrapperPlayServerSpawnEntity spawn = new WrapperPlayServerSpawnEntity(event);
                entityId = spawn.getEntityId();
                if (spawn.getUUID().isPresent()) {
                    Player target = org.bukkit.Bukkit.getPlayer(spawn.getUUID().get());
                    if (target != null && !ArenaVisibilityManager.canSee(receiver, target)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
                entityId = new WrapperPlayServerEntityTeleport(event).getEntityId();
            } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_VELOCITY) {
                entityId = new WrapperPlayServerEntityVelocity(event).getEntityId();
            } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
                entityId = new WrapperPlayServerEntityMetadata(event).getEntityId();
            } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_STATUS) {
                entityId = new WrapperPlayServerEntityStatus(event).getEntityId();
            } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_SOUND_EFFECT) {
                entityId = new WrapperPlayServerEntitySoundEffect(event).getEntityId();
            } else if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO_UPDATE) {
                com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate info = new com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate(event);
                info.getEntries().removeIf(entry -> {
                    Player target = org.bukkit.Bukkit.getPlayer(entry.getProfileId());
                    return target != null && !ArenaVisibilityManager.canSee(receiver, target);
                });
                if (info.getEntries().isEmpty()) {
                    event.setCancelled(true);
                }
                return;
            } else if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO_REMOVE) {
                // Do not intercept remove, let them be removed from tab list
            }

            if (entityId != -1) {
                Duel entityDuel = ArenaVisibilityManager.getEntityDuel(entityId);
                if (entityDuel != null) {
                    Duel receiverDuel = DuelManager.getInstance().getDuel(receiver);
                    Duel receiverSpectating = ArenaVisibilityManager.getSpectatingDuel(receiver);

                    boolean canSee = false;
                    if (receiverDuel != null && receiverDuel.equals(entityDuel)) {
                        canSee = true;
                    } else if (receiverSpectating != null && receiverSpectating.equals(entityDuel)) {
                        canSee = true;
                    }

                    if (!canSee) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
