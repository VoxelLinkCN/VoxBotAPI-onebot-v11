package dev.voxellink.api.discord;

/** Utilities for Discord's decimal snowflake identifiers. */
public final class DiscordSnowflake {
    private DiscordSnowflake() {
    }

    /**
     * Parses a snowflake that fits Java's signed {@code long} range.
     * Discord's currently issued IDs fit this range; larger future values are
     * rejected instead of silently wrapping to a negative number.
     */
    public static long parse(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Discord snowflake must not be empty");
        }
        try {
            long parsed = Long.parseLong(value);
            return requireValid(parsed);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Discord snowflake is outside Java long range: " + value, e);
        }
    }

    public static long requireValid(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Discord snowflake must be a positive long: " + value);
        }
        return value;
    }
}
