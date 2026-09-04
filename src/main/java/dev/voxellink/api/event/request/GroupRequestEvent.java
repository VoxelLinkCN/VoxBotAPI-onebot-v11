package dev.voxellink.api.event.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class GroupRequestEvent extends RequestEvent {
    private final GroupRequestType subType;
    private final long groupId;

    public GroupRequestEvent(long time, long selfId, GroupRequestType subType, long groupId, long userId, String comment, String flag) {
        super(time, selfId, RequestType.GROUP, userId, comment, flag);
        this.subType = subType;
        this.groupId = groupId;
    }
}
