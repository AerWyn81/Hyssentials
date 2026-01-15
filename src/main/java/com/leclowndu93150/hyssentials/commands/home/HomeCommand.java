package com.leclowndu93150.hyssentials.commands.home;

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
import com.leclowndu93150.hyssentials.manager.HomeManager;
import com.leclowndu93150.hyssentials.manager.RankManager;
import com.leclowndu93150.hyssentials.manager.TeleportWarmupManager;
import com.leclowndu93150.hyssentials.util.Permissions;
import java.util.UUID;
import javax.annotation.Nonnull;

public class HomeCommand extends AbstractPlayerCommand {
    private final HomeManager homeManager;
    private final TeleportWarmupManager warmupManager;
    private final CooldownManager cooldownManager;
    private final RankManager rankManager;
    private final RequiredArg<String> nameArg = this.withRequiredArg("name", "Home name", ArgTypes.STRING);

    public HomeCommand(@Nonnull HomeManager homeManager, @Nonnull TeleportWarmupManager warmupManager,
                       @Nonnull CooldownManager cooldownManager, @Nonnull RankManager rankManager) {
        super("home", "Teleport to your home");
        this.homeManager = homeManager;
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
        CommandSettings settings = rankManager.getEffectiveSettings(playerRef, CooldownManager.HOME);
        boolean bypassCooldown = Permissions.canBypassCooldown(playerRef);

        if (!settings.isEnabled()) {
            context.sendMessage(Message.raw("You don't have permission to use /home."));
            return;
        }

        if (!bypassCooldown && cooldownManager.isOnCooldown(playerUuid, CooldownManager.HOME, settings.getCooldownSeconds())) {
            long remaining = cooldownManager.getCooldownRemaining(playerUuid, CooldownManager.HOME, settings.getCooldownSeconds());
            context.sendMessage(Message.raw(String.format("You must wait %d seconds before using /home again.", remaining)));
            return;
        }

        LocationData home = homeManager.getHome(playerUuid, name);
        if (home == null) {
            context.sendMessage(Message.raw(String.format("Home '%s' not found. Use /sethome %s to set it.", name, name)));
            return;
        }

        int warmupSeconds = bypassCooldown ? 0 : settings.getWarmupSeconds();
        warmupManager.startWarmup(playerRef, store, ref, world, home, warmupSeconds, CooldownManager.HOME, "home '" + name + "'", null);
    }
}
