package dev.voxellink.api.discord.client;

import dev.voxellink.api.discord.data.DiscordMessage;
import dev.voxellink.api.listener.VBotListener;

import java.util.concurrent.CompletableFuture;

/**
 * Discord-specific bot client. Discord channels and snowflake IDs do not map
 * cleanly to OneBot groups, so they intentionally have a separate API.
 */
public interface DiscordClient {
    void connect();

    void disconnect();

    /** Permanently closes this client and its background resources. */
    void shutdown();

    void reconnect();

    boolean isConnected();

    void addListener(VBotListener listener);

    void removeListener(VBotListener listener);

    boolean hasListener(VBotListener listener);

    CompletableFuture<DiscordMessage> sendMessage(long channelId, String content);

    CompletableFuture<DiscordMessage> editMessage(long channelId, long messageId, String content);

    CompletableFuture<Void> deleteMessage(long channelId, long messageId);
}
