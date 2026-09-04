package dev.voxellink.api.discord;

import dev.voxellink.api.discord.client.DiscordClient;
import dev.voxellink.api.discord.client.DiscordGatewayClient;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class DiscordClientApiTest {
    @Test
    void factoryExposesDedicatedClientInterface() {
        DiscordClient client = DiscordClients.gateway("test-token");
        assertInstanceOf(DiscordGatewayClient.class, client);
        assertFalse(client.isConnected());
    }

    @Test
    void rejectsEmptyTokenAndInvalidMessageArgumentsBeforeNetworkCall() {
        assertThrows(IllegalArgumentException.class, () -> DiscordClients.gateway(" "));
        DiscordGatewayClient client = DiscordClients.gateway("test-token");
        assertThrows(IllegalArgumentException.class, () -> client.sendMessage(0, "hello"));
        assertThrows(IllegalArgumentException.class, () -> client.sendMessage(1, ""));
        assertThrows(IllegalArgumentException.class, () -> client.editMessage(1, -1, "hello"));
    }

    @Test
    void supportsCustomGatewayAndNormalizedApiBase() {
        DiscordGatewayClient client = DiscordClients.gateway(
                "test-token",
                DiscordGatewayClient.DEFAULT_INTENTS,
                URI.create("ws://127.0.0.1:8080/gateway?v=10&encoding=json"),
                URI.create("http://127.0.0.1:8080/discord/api/v10///")
        );
        assertEquals("ws://127.0.0.1:8080/gateway?v=10&encoding=json", client.getURI().toString());
        assertEquals("http://127.0.0.1:8080/discord/api/v10", client.getApiBase().toString());
        assertThrows(IllegalArgumentException.class, () -> new DiscordGatewayClient(
                URI.create("ws://localhost"), URI.create("ws://localhost/api"), "token", 0));
        client.shutdown();
    }
}
