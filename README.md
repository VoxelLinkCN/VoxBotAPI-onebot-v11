# VoxBotAPI

VoxBotAPI provides bot protocol clients for VoxBot. The existing OneBot 11 API remains under `dev.voxellink.api.client`, `action`, and `event`. Discord support is an independent adapter under `dev.voxellink.api.discord` and does not translate Discord channels into OneBot groups.

## Discord quick start

Discord Gateway v10 and REST v10 are supported on the project's existing Java 8 baseline.

```java
DiscordGatewayClient discord = DiscordClients.gateway(System.getenv("DISCORD_BOT_TOKEN"));
discord.addListener(new VBotListener() {
    @VBotEventHandler
    public void onReady(DiscordReadyEvent event) {
        System.out.println("Discord ready as " + event.getUser().getUsername());
    }

    @VBotEventHandler
    public void onMessage(DiscordMessageEvent event) {
        if ("MESSAGE_CREATE".equals(event.getEventName())) {
            System.out.println(event.getMessage().getContent());
        }
    }
});
discord.connect();

discord.sendMessage(123456789012345678L, "Hello from VoxBotAPI")
       .thenAccept(message -> System.out.println(message.getId()));
```

The default intents include guilds, guild messages, direct messages, and message content. Enable the **Message Content Intent** for the bot in the Discord Developer Portal, or pass a custom intent bitset to `DiscordClients.gateway(token, intents)`.

Custom HTTP/WebSocket proxy endpoints can be supplied without changing the default constructors:

```java
DiscordGatewayClient proxied = DiscordClients.gateway(
        token,
        DiscordGatewayClient.DEFAULT_INTENTS,
        URI.create("wss://proxy.example/gateway?v=10&encoding=json"),
        URI.create("https://proxy.example/api/v10")
);
```

Available Discord events are `DiscordReadyEvent`, `DiscordMessageEvent` (`MESSAGE_CREATE` and `MESSAGE_UPDATE`), `DiscordMessageDeleteEvent`, and the base `DiscordEvent` for all other dispatches. `MESSAGE_UPDATE` is partial: absent content, author, and guild fields are returned as `null`. Direct messages also have a `null` guild ID.

## Current Discord limits

- Text messages only; embeds, attachments, components, reactions, threads, bulk delete, and interaction commands are not exposed yet.
- Gateway zlib compression, voice, sharding, and session resume are not implemented. Recoverable disconnects reconnect and identify a fresh session.
- REST 429 responses are retried up to three times using Discord's `retry_after`, capped at 60 seconds per wait. Other HTTP failures use `DiscordException`; transport/timeout failures use status `-1`.
- IDs use Java `long`. Values from `1` through `Long.MAX_VALUE` are accepted. Larger unsigned 64-bit snowflakes are rejected explicitly instead of wrapping; Discord is not expected to issue such IDs before the signed range is exhausted.
- The bot token is used only in authentication payloads/headers and is never included in library log messages.
