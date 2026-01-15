package com.leclowndu93150.hyssentials.commands.tpa;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.data.TpaRequest;
import com.leclowndu93150.hyssentials.data.TpaSettings;
import com.leclowndu93150.hyssentials.manager.RankManager;
import com.leclowndu93150.hyssentials.manager.TpaManager;
import java.util.UUID;
import javax.annotation.Nonnull;

public class TpahereCommand extends AbstractPlayerCommand {
    private final TpaManager tpaManager;
    private final RankManager rankManager;
    private final RequiredArg<String> targetArg = this.withRequiredArg("player", "Target player name", ArgTypes.STRING);

    public TpahereCommand(@Nonnull TpaManager tpaManager, @Nonnull RankManager rankManager) {
        super("tpahere", "Request a player to teleport to you");
        this.tpaManager = tpaManager;
        this.rankManager = rankManager;
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        String targetName = targetArg.get(context);
        UUID senderUuid = playerRef.getUuid();
        TpaSettings settings = rankManager.getEffectiveTpaSettings(playerRef);

        if (!settings.isEnabled()) {
            context.sendMessage(Message.raw("You don't have permission to use /tpahere."));
            return;
        }

        if (tpaManager.isOnCooldown(senderUuid, settings.getCooldownSeconds())) {
            long remaining = tpaManager.getCooldownRemaining(senderUuid, settings.getCooldownSeconds());
            context.sendMessage(Message.raw(String.format("You must wait %d seconds before sending another request.", remaining)));
            return;
        }
        PlayerRef targetPlayer = Universe.get().getPlayerByUsername(targetName, NameMatching.STARTS_WITH_IGNORE_CASE);
        if (targetPlayer == null) {
            context.sendMessage(Message.raw("Player not found: " + targetName));
            return;
        }
        if (targetPlayer.getUuid().equals(senderUuid)) {
            context.sendMessage(Message.raw("You cannot send a teleport request to yourself."));
            return;
        }
        boolean sent = tpaManager.sendRequest(senderUuid, targetPlayer.getUuid(), TpaRequest.TpaType.TPAHERE, settings.getTimeoutSeconds());
        if (!sent) {
            context.sendMessage(Message.raw("You already have a pending request to this player."));
            return;
        }
        tpaManager.setCooldown(senderUuid);
        context.sendMessage(Message.raw(String.format("Teleport request sent to %s.", targetPlayer.getUsername())));
        targetPlayer.sendMessage(Message.raw(String.format(
            "%s has requested you to teleport to them. Type /tpaccept to accept or /tpdeny to deny. Expires in %d seconds.",
            playerRef.getUsername(), settings.getTimeoutSeconds())));
    }
}
