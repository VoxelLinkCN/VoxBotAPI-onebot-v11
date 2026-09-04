package dev.voxellink.api.discord.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONObject;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class DiscordMessageDeleteEvent extends DiscordEvent {
    private final long messageId;
    private final long channelId;
    private final Long guildId;

    public DiscordMessageDeleteEvent(long time, long selfId, long messageId, long channelId, Long guildId, JSONObject rawData) {
        super(time, selfId, "MESSAGE_DELETE", rawData);
        this.messageId = messageId;
        this.channelId = channelId;
        this.guildId = guildId;
    }
}
