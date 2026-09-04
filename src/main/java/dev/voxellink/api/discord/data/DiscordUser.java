package dev.voxellink.api.discord.data;

import dev.voxellink.api.discord.DiscordSnowflake;
import lombok.Data;
import org.json.JSONObject;

@Data
public class DiscordUser {
    private final long id;
    private final String username;
    private final String globalName;
    private final boolean bot;

    public static DiscordUser from(JSONObject data) {
        return new DiscordUser(
                DiscordSnowflake.parse(data.getString("id")),
                data.optString("username", ""),
                data.isNull("global_name") ? null : data.optString("global_name", null),
                data.optBoolean("bot", false)
        );
    }
}
