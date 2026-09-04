package dev.voxellink.api.discord.data;

import dev.voxellink.api.discord.DiscordSnowflake;
import lombok.Data;
import org.json.JSONObject;

@Data
public class DiscordMessage {
    private final long id;
    private final long channelId;
    private final Long guildId;
    private final String content;
    private final DiscordUser author;
    private final JSONObject rawData;

    public static DiscordMessage from(JSONObject data) {
        return new DiscordMessage(
                DiscordSnowflake.parse(data.getString("id")),
                DiscordSnowflake.parse(data.getString("channel_id")),
                data.has("guild_id") && !data.isNull("guild_id") ? DiscordSnowflake.parse(data.getString("guild_id")) : null,
                data.has("content") && !data.isNull("content") ? data.getString("content") : null,
                data.has("author") ? DiscordUser.from(data.getJSONObject("author")) : null,
                data
        );
    }
}
