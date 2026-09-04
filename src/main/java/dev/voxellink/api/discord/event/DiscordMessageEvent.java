package dev.voxellink.api.discord.event;

import dev.voxellink.api.discord.data.DiscordMessage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONObject;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class DiscordMessageEvent extends DiscordEvent {
    private final DiscordMessage message;

    public DiscordMessageEvent(long time, long selfId, String eventName, DiscordMessage message, JSONObject rawData) {
        super(time, selfId, eventName, rawData);
        this.message = message;
    }
}
