package com.leclowndu93150.hyssentials.commands.warp;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.data.CommandSettings;
import com.leclowndu93150.hyssentials.data.LocationData;
import com.leclowndu93150.hyssentials.manager.CooldownManager;
import com.leclowndu93150.hyssentials.manager.RankManager;
import com.leclowndu93150.hyssentials.manager.TeleportWarmupManager;
import com.leclowndu93150.hyssentials.manager.WarpManager;
import com.leclowndu93150.hyssentials.util.Permissions;
import java.util.UUID;
import javax.annotation.Nonnull;

public class WarpCommand extends AbstractPlayerCommand {
    private final WarpManager warpManager;
    private final TeleportWarmupManager warmupManager;
    private final CooldownManager cooldownManager;
    private final RankManager rankManager;
    private final RequiredArg<String> nameArg = this.withRequiredArg("name", "Warp name", ArgTypes.STRING);

    public WarpCommand(@Nonnull WarpManager warpManager, @Nonnull TeleportWarmupManager warmupManager,
                       @Nonnull CooldownManager cooldownManager, @Nonnull RankManager rankManager) {
        super("warp", "Teleport to a server warp");
        this.warpManager = warpManager;
        this.warmupManager = warmupManager;
        this.cooldownManager = cooldownManager;
        this.rankManager = rankManager;
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        String name = nameArg.get(context);
        UUID playerUuid = playerRef.getUuid();
        CommandSettings settings = rankManager.getEffectiveSettings(playerRef, CooldownManager.WARP);
        boolean bypassCooldown = Permissions.canBypassCooldown(playerRef);

        if (!settings.isEnabled()) {
            context.sendMessage(Message.raw("You don't have permission to use /warp."));
            return;
        }

        if (!bypassCooldown && cooldownManager.isOnCooldown(playerUuid, CooldownManager.WARP, settings.getCooldownSeconds())) {
            long remaining = cooldownManager.getCooldownRemaining(playerUuid, CooldownManager.WARP, settings.getCooldownSeconds());
            context.sendMessage(Message.raw(String.format("You must wait %d seconds before using /warp again.", remaining)));
            return;
        }

        LocationData warp = warpManager.getWarp(name);
        if (warp == null) {
            context.sendMessage(Message.raw(String.format("Warp '%s' not found. Use /warps to see available warps.", name)));
            return;
        }

        int warmupSeconds = bypassCooldown ? 0 : settings.getWarmupSeconds();
        warmupManager.startWarmup(playerRef, store, ref, world, warp, warmupSeconds, CooldownManager.WARP, "warp '" + name + "'", null);
    }
}
