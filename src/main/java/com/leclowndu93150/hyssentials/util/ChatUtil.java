package com.leclowndu93150.hyssentials.util;

import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.leclowndu93150.chatcustomization.util.ColorUtil;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utility for sending formatted messages that integrates with ChatCustomization if available.
 * Falls back to vanilla Message formatting if ChatCustomization is not installed.
 */
public final class ChatUtil {
    private static final PluginIdentifier CHAT_CUSTOMIZATION_ID = new PluginIdentifier("com.leclowndu93150", "ChatCustomization");

    private ChatUtil() {}

    /**
     * Checks if ChatCustomization mod is available.
     */
    public static boolean isChatCustomizationAvailable() {
        PluginManager pm = PluginManager.get();
        return pm != null && pm.getPlugin(CHAT_CUSTOMIZATION_ID) != null;
    }

    /**
     * Converts a color string to hex format.
     * If ChatCustomization is available, supports named colors (RED, GOLD, etc.).
     * Otherwise, returns the color as-is (should already be hex).
     */
    @Nonnull
    public static String toHex(@Nonnull String color) {
        if (isChatCustomizationAvailable()) {
            return ColorUtil.toHex(color);
        }

        if (color.startsWith("#") && color.length() == 7) {
            return color.toUpperCase();
        }
        return "#FFFFFF";
    }

    /**
     * Sends a formatted message to a single player.
     */
    public static void sendMessage(@Nonnull PlayerRef player, @Nonnull Message message) {
        player.sendMessage(message);
    }

    /**
     * Sends a formatted message to multiple players.
     */
    public static void broadcastMessage(@Nonnull Collection<PlayerRef> players, @Nonnull Message message) {
        for (PlayerRef player : players) {
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

    /**
     * Creates a colored message.
     */
    @Nonnull
    public static Message colored(@Nonnull String text, @Nonnull String color) {
        return Message.raw(text).color(toHex(color));
    }

    /**
     * Creates a message with a colored prefix.
     */
    @Nonnull
    public static Message prefixed(@Nonnull String prefix, @Nonnull String prefixColor,
                                   @Nonnull String text, @Nullable String textColor) {
        Message msg = Message.empty()
            .insert(Message.raw("[" + prefix + "] ").color(toHex(prefixColor)));

        if (textColor != null) {
            msg.insert(Message.raw(text).color(toHex(textColor)));
        } else {
            msg.insert(Message.raw(text));
        }

        return msg;
    }

    /**
     * Creates an admin chat style message.
     */
    @Nonnull
    public static Message adminChat(@Nonnull String prefix, @Nonnull String prefixColor,
                                    @Nonnull String senderName, @Nonnull String message,
                                    @Nonnull String messageColor) {
        return Message.empty()
            .insert(Message.raw("[" + prefix + "] ").color(toHex(prefixColor)))
            .insert(Message.raw(senderName + ": "))
            .insert(Message.raw(message).color(toHex(messageColor)));
    }

    /**
     * Creates a private message format for the sender view.
     */
    @Nonnull
    public static Message privateMessageTo(@Nonnull String recipientName, @Nonnull String message) {
        return Message.empty()
            .insert(Message.raw("[To " + recipientName + "] ").color("#AAAAAA"))
            .insert(Message.raw(message).color("#FFFFFF"));
    }

    /**
     * Creates a private message format for the recipient view.
     */
    @Nonnull
    public static Message privateMessageFrom(@Nonnull String senderName, @Nonnull String message) {
        return Message.empty()
            .insert(Message.raw("[From " + senderName + "] ").color("#AAAAAA"))
            .insert(Message.raw(message).color("#FFFFFF"));
    }
}
