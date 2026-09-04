package dev.voxellink.api.discord.event;

import dev.voxellink.api.discord.data.DiscordUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONObject;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class DiscordReadyEvent extends DiscordEvent {
    private final DiscordUser user;
    private final String sessionId;

    public DiscordReadyEvent(long time, long selfId, DiscordUser user, String sessionId, JSONObject rawData) {
        super(time, selfId, "READY", rawData);
        this.user = user;
        this.sessionId = sessionId;
    }
}
