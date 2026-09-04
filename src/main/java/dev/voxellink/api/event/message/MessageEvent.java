package dev.voxellink.api.event.message;

import dev.voxellink.api.event.NEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONArray;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class MessageEvent extends NEvent {
    private final MessageType messageType;
    private final int messageId;
    private final long senderId;
    private final JSONArray message;
    private final String rawMessage;

    protected MessageEvent(long time, long selfId, MessageType messageType, int messageId, long senderId, JSONArray message, String rawMessage) {
        super(time, selfId);
        this.messageType = messageType;
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.rawMessage = rawMessage;
    }
}
