package dev.voxellink.api.event.notice;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class GroupIncreaseEvent extends NoticeEvent {
    private final GroupIncreaseType subType;
    private final long groupId;
    private final long operatorId;
    private final long userId;

    public GroupIncreaseEvent(long time, long selfId, GroupIncreaseType subType, long groupId, long operatorId, long userId) {
        super(time, selfId, NoticeType.GROUP_INCREASE);
        this.subType = subType;
        this.groupId = groupId;
        this.operatorId = operatorId;
        this.userId = userId;
    }
}
