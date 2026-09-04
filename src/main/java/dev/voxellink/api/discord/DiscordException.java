package dev.voxellink.api.discord;

public class DiscordException extends RuntimeException {
    private final int statusCode;

    public DiscordException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
