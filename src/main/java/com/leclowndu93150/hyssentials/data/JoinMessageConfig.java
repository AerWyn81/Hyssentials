package com.leclowndu93150.hyssentials.data;

import javax.annotation.Nonnull;

public record JoinMessageConfig(
    boolean enabled,
    @Nonnull String joinMessage,
    @Nonnull String leaveMessage
) {
    public static final String DEFAULT_JOIN_MESSAGE = "{player} has joined {world}";
    public static final String DEFAULT_LEAVE_MESSAGE = "{player} has left {world}";

    public static JoinMessageConfig createDefault() {
        return new JoinMessageConfig(false, DEFAULT_JOIN_MESSAGE, DEFAULT_LEAVE_MESSAGE);
    }

    public String formatJoinMessage(@Nonnull String playerName, @Nonnull String worldName) {
        return joinMessage
            .replace("{player}", playerName)
            .replace("{world}", worldName);
    }

    public String formatLeaveMessage(@Nonnull String playerName, @Nonnull String worldName) {
        return leaveMessage
            .replace("{player}", playerName)
            .replace("{world}", worldName);
    }
}
