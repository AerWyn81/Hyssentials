package com.leclowndu93150.hyssentials.commands.tpa;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.data.LocationData;
import com.leclowndu93150.hyssentials.data.TpaRequest;
import com.leclowndu93150.hyssentials.data.TpaSettings;
import com.leclowndu93150.hyssentials.manager.CooldownManager;
import com.leclowndu93150.hyssentials.manager.RankManager;
import com.leclowndu93150.hyssentials.manager.TeleportWarmupManager;
import com.leclowndu93150.hyssentials.manager.TpaManager;
import java.util.UUID;
import javax.annotation.Nonnull;

public class TpacceptCommand extends AbstractPlayerCommand {
    private final TpaManager tpaManager;
    private final TeleportWarmupManager warmupManager;
    private final RankManager rankManager;

    public TpacceptCommand(@Nonnull TpaManager tpaManager, @Nonnull TeleportWarmupManager warmupManager, @Nonnull RankManager rankManager) {
        super("tpaccept", "Accept a pending teleport request");
        this.tpaManager = tpaManager;
        this.warmupManager = warmupManager;
        this.rankManager = rankManager;
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        UUID targetUuid = playerRef.getUuid();
        TpaSettings settings = rankManager.getEffectiveTpaSettings(playerRef);
        TpaRequest request = tpaManager.acceptRequest(targetUuid, settings.getTimeoutSeconds());
        if (request == null) {
            context.sendMessage(Message.raw("You have no pending teleport requests."));
            return;
        }
        PlayerRef senderPlayer = Universe.get().getPlayer(request.sender());
        if (senderPlayer == null) {
            context.sendMessage(Message.raw("The player who sent the request is no longer online."));
            return;
        }
        Ref<EntityStore> senderRef = senderPlayer.getReference();
        if (senderRef == null || !senderRef.isValid()) {
            context.sendMessage(Message.raw("The player who sent the request is no longer available."));
            return;
        }
        Store<EntityStore> senderStore = senderRef.getStore();
        World senderWorld = senderStore.getExternalData().getWorld();

        if (request.type() == TpaRequest.TpaType.TPA) {
            TransformComponent targetTransform = store.getComponent(ref, TransformComponent.getComponentType());
            HeadRotation targetHeadRot = store.getComponent(ref, HeadRotation.getComponentType());
            if (targetTransform == null) {
                context.sendMessage(Message.raw("Could not get your position."));
                return;
            }
            Vector3d targetPos = targetTransform.getPosition().clone();
            Vector3f targetRot = targetHeadRot != null ? targetHeadRot.getRotation().clone() : new Vector3f(0, 0, 0);
            LocationData destination = new LocationData(world.getName(), targetPos.getX(), targetPos.getY(), targetPos.getZ(), targetRot.getPitch(), targetRot.getYaw());

            TpaSettings senderSettings = rankManager.getEffectiveTpaSettings(senderPlayer);
            warmupManager.startWarmup(senderPlayer, senderStore, senderRef, senderWorld, destination, senderSettings.getWarmupSeconds(), CooldownManager.TPA, playerRef.getUsername(), null);
            context.sendMessage(Message.raw(String.format("Teleport request from %s accepted.", senderPlayer.getUsername())));
        } else {
            TransformComponent senderTransform = senderStore.getComponent(senderRef, TransformComponent.getComponentType());
            HeadRotation senderHeadRot = senderStore.getComponent(senderRef, HeadRotation.getComponentType());
            if (senderTransform == null) {
                context.sendMessage(Message.raw("Could not get sender's position."));
                return;
            }
            Vector3d senderPos = senderTransform.getPosition().clone();
            Vector3f senderRot = senderHeadRot != null ? senderHeadRot.getRotation().clone() : new Vector3f(0, 0, 0);
            LocationData destination = new LocationData(senderWorld.getName(), senderPos.getX(), senderPos.getY(), senderPos.getZ(), senderRot.getPitch(), senderRot.getYaw());

            warmupManager.startWarmup(playerRef, store, ref, world, destination, settings.getWarmupSeconds(), CooldownManager.TPA, senderPlayer.getUsername(), null);
            senderPlayer.sendMessage(Message.raw(String.format("%s accepted your teleport request.", playerRef.getUsername())));
        }
    }
}
