package dev.voxellink.api.event.message;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class GroupMessageEvent extends MessageEvent {
    private final GroupMessageType subType;
    private final long groupId;
    private final long anonymousId;
    private final @Nullable String anonymousName;

    public GroupMessageEvent(long time, long selfId, int messageId, long senderId, JSONArray message, String rawMessage, GroupMessageType subType, long groupId, long anonymousId, @Nullable String anonymousName) {
        super(time, selfId, MessageType.GROUP, messageId, senderId, message, rawMessage);
        this.subType = subType;
        this.groupId = groupId;
        this.anonymousId = anonymousId;
        this.anonymousName = anonymousName;
    }
}
