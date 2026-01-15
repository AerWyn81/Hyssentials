package com.leclowndu93150.hyssentials.commands.admin;

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
import com.leclowndu93150.hyssentials.data.Rank;
import com.leclowndu93150.hyssentials.manager.HomeManager;
import com.leclowndu93150.hyssentials.manager.RankManager;
import com.leclowndu93150.hyssentials.util.Permissions;
import javax.annotation.Nonnull;

public class PlayerInfoSubCommand extends AbstractPlayerCommand {
    private final RankManager rankManager;
    private final HomeManager homeManager;
    private final RequiredArg<String> playerArg = this.withRequiredArg("player", "Target player", ArgTypes.STRING);

    public PlayerInfoSubCommand(@Nonnull RankManager rankManager, @Nonnull HomeManager homeManager) {
        super("playerinfo", "View player rank and stats");
        this.rankManager = rankManager;
        this.homeManager = homeManager;
        this.requirePermission(Permissions.ADMIN_PLAYERINFO);
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef sender, @Nonnull World world) {
        if (!Permissions.canViewPlayerInfo(sender)) {
            context.sendMessage(Message.raw("You don't have permission to view player info."));
            return;
        }

        String targetName = playerArg.get(context);
        PlayerRef targetPlayer = Universe.get().getPlayerByUsername(targetName, NameMatching.STARTS_WITH_IGNORE_CASE);
        if (targetPlayer == null) {
            context.sendMessage(Message.raw("Player not found: " + targetName));
            return;
        }

        Rank rank = rankManager.getPlayerRank(targetPlayer);
        int homeCount = homeManager.getHomeCount(targetPlayer.getUuid());
        int maxHomes = rankManager.getEffectiveMaxHomes(targetPlayer);

        context.sendMessage(Message.raw(String.format("=== Player Info: %s ===", targetPlayer.getUsername())));
        context.sendMessage(Message.raw(String.format("  UUID: %s", targetPlayer.getUuid())));
        context.sendMessage(Message.raw(String.format("  Rank: %s (id: %s)", rank.getDisplayName(), rank.getId())));
        context.sendMessage(Message.raw(String.format("  Permission: %s", rank.getPermission())));
        context.sendMessage(Message.raw(String.format("  Homes: %d/%d", homeCount, maxHomes)));
        context.sendMessage(Message.raw(String.format("  Home Cooldown: %ds", rank.getHomeSettings().getCooldownSeconds())));
        context.sendMessage(Message.raw(String.format("  Warmup: %ds", rank.getHomeSettings().getWarmupSeconds())));
    }
}
