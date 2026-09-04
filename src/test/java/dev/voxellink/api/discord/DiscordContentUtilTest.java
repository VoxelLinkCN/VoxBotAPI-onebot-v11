package dev.voxellink.api.discord;

import dev.voxellink.api.discord.data.DiscordMessage;
import dev.voxellink.api.discord.event.DiscordEvent;
import dev.voxellink.api.discord.event.DiscordMessageDeleteEvent;
import dev.voxellink.api.discord.event.DiscordMessageEvent;
import dev.voxellink.api.discord.event.DiscordReadyEvent;
import dev.voxellink.api.discord.util.DiscordContentUtil;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiscordContentUtilTest {
    @Test
    void parsesReadyAndUsesBotId() {
        JSONObject data = new JSONObject()
                .put("session_id", "session")
                .put("user", new JSONObject()
                        .put("id", "9223372036854775807")
                        .put("username", "VoxBot")
                        .put("global_name", JSONObject.NULL)
                        .put("bot", true));

        DiscordReadyEvent event = (DiscordReadyEvent) DiscordContentUtil.parseDispatch("READY", data, 0);
        assertEquals(Long.MAX_VALUE, event.getSelfId());
        assertEquals("session", event.getSessionId());
        assertTrue(event.getUser().isBot());
    }

    @Test
    void parsesDirectMessageWithoutGuild() {
        JSONObject data = messageData().put("content", "hello");
        DiscordMessageEvent event = (DiscordMessageEvent) DiscordContentUtil.parseDispatch("MESSAGE_CREATE", data, 99);

        DiscordMessage message = event.getMessage();
        assertNull(message.getGuildId());
        assertEquals("hello", message.getContent());
        assertEquals(99, event.getSelfId());
    }

    @Test
    void acceptsPartialMessageUpdate() {
        DiscordMessageEvent event = (DiscordMessageEvent) DiscordContentUtil.parseDispatch(
                "MESSAGE_UPDATE", messageData(), 99);

        assertNull(event.getMessage().getContent());
        assertNull(event.getMessage().getAuthor());
        assertNull(event.getMessage().getGuildId());
    }

    @Test
    void parsesMessageDeleteAndUnknownDispatch() {
        JSONObject deleted = new JSONObject()
                .put("id", "123")
                .put("channel_id", "456")
                .put("guild_id", "789");
        DiscordMessageDeleteEvent event = (DiscordMessageDeleteEvent) DiscordContentUtil.parseDispatch(
                "MESSAGE_DELETE", deleted, 99);
        assertEquals(123, event.getMessageId());
        assertEquals(Long.valueOf(789), event.getGuildId());

        DiscordEvent unknown = DiscordContentUtil.parseDispatch("GUILD_CREATE", new JSONObject(), 99);
        assertEquals("GUILD_CREATE", unknown.getEventName());
    }

    @Test
    void validatesSignedLongSnowflakeBoundary() {
        assertEquals(Long.MAX_VALUE, DiscordSnowflake.parse(Long.toString(Long.MAX_VALUE)));
        assertThrows(IllegalArgumentException.class, () -> DiscordSnowflake.parse("9223372036854775808"));
        assertThrows(IllegalArgumentException.class, () -> DiscordSnowflake.parse("0"));
        assertThrows(IllegalArgumentException.class, () -> DiscordSnowflake.requireValid(-1));
    }

    private static JSONObject messageData() {
        return new JSONObject()
                .put("id", "123")
                .put("channel_id", "456");
    }
}
