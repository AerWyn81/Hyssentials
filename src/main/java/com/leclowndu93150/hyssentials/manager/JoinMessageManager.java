package com.leclowndu93150.hyssentials.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.DrainPlayerFromWorldEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.leclowndu93150.hyssentials.data.JoinMessageConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class JoinMessageManager {
    private static final String CONFIG_FILE = "joinmessages.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path dataDirectory;
    private final HytaleLogger logger;
    private JoinMessageConfig config;

    public JoinMessageManager(@Nonnull Path dataDirectory, @Nonnull HytaleLogger logger) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        load();
    }

    public void load() {
        Path file = dataDirectory.resolve(CONFIG_FILE);
        if (Files.exists(file)) {
            try {
                String json = Files.readString(file);
                config = GSON.fromJson(json, JoinMessageConfig.class);
                if (config == null) {
                    config = JoinMessageConfig.createDefault();
                }
            } catch (IOException e) {
                logger.atSevere().log("Failed to load join message config: %s", e.getMessage());
                config = JoinMessageConfig.createDefault();
            }
        } else {
            config = JoinMessageConfig.createDefault();
            save();
        }
    }

    public void save() {
        try {
            if (!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }
            Path file = dataDirectory.resolve(CONFIG_FILE);
            String json = GSON.toJson(config);
            Files.writeString(file, json);
        } catch (IOException e) {
            logger.atSevere().log("Failed to save join message config: %s", e.getMessage());
        }
    }

    public void reload() {
        load();
    }

    @Nonnull
    public JoinMessageConfig getConfig() {
        return config;
    }

    public void onPlayerJoinWorld(@Nonnull AddPlayerToWorldEvent event) {
        if (!config.enabled()) {
            return;
        }

        event.setBroadcastJoinMessage(false);

        World world = event.getWorld();
        PlayerRef playerRef = event.getHolder().getComponent(PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }

        String playerName = playerRef.getUsername();
        String worldName = getWorldDisplayName(world);
        String formattedMessage = config.formatJoinMessage(playerName, worldName);

        for (PlayerRef player : world.getPlayerRefs()) {
            player.sendMessage(Message.raw(formattedMessage));
        }
        playerRef.sendMessage(Message.raw(formattedMessage));
    }

    public void onPlayerLeaveWorld(@Nonnull DrainPlayerFromWorldEvent event) {
        if (!config.enabled()) {
            return;
        }

        World world = event.getWorld();
        PlayerRef playerRef = event.getHolder().getComponent(PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }

        String playerName = playerRef.getUsername();
        String worldName = getWorldDisplayName(world);
        String formattedMessage = config.formatLeaveMessage(playerName, worldName);

        for (PlayerRef player : world.getPlayerRefs()) {
            if (!player.getUuid().equals(playerRef.getUuid())) {
                player.sendMessage(Message.raw(formattedMessage));
            }
        }
    }

    private String getWorldDisplayName(@Nonnull World world) {
        WorldConfig worldConfig = world.getWorldConfig();
        if (worldConfig.getDisplayName() != null) {
            return worldConfig.getDisplayName();
        }
        return WorldConfig.formatDisplayName(world.getName());
    }
}
