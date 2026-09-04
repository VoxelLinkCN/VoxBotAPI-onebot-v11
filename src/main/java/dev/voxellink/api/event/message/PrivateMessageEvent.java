package dev.voxellink.api.event.message;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONArray;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class PrivateMessageEvent extends MessageEvent {
    private final PrivateMessageType subType;

    public PrivateMessageEvent(long time, long selfId, int messageId, long senderId, JSONArray message, String rawMessage, PrivateMessageType subType) {
        super(time, selfId, MessageType.PRIVATE, messageId, senderId, message, rawMessage);
        this.subType = subType;
    }
}
