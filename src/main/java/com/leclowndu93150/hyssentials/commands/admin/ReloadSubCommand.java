package com.leclowndu93150.hyssentials.commands.admin;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import com.leclowndu93150.hyssentials.config.HyssentialsConfig;
import com.leclowndu93150.hyssentials.manager.RankManager;
import com.leclowndu93150.hyssentials.util.Permissions;
import javax.annotation.Nonnull;

public class ReloadSubCommand extends AbstractPlayerCommand {
    private final RankManager rankManager;
    private final Config<HyssentialsConfig> config;

    public ReloadSubCommand(@Nonnull RankManager rankManager, @Nonnull Config<HyssentialsConfig> config) {
        super("reload", "Reload configuration and ranks");
        this.rankManager = rankManager;
        this.config = config;
        this.requirePermission(Permissions.ADMIN_RELOAD);
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef sender, @Nonnull World world) {
        if (!Permissions.canReload(sender)) {
            context.sendMessage(Message.raw("You don't have permission to reload configuration."));
            return;
        }

        try {
            config.load().join();
            rankManager.reload();
            context.sendMessage(Message.raw("Configuration and ranks reloaded successfully."));
        } catch (Exception e) {
            context.sendMessage(Message.raw("Error reloading configuration: " + e.getMessage()));
        }
    }
}
