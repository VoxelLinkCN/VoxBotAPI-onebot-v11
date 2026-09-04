package dev.voxellink.api.event.notice;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class PokeEvent extends NoticeEvent {
    private final long groupId;
    private final long userId;
    private final long targetId;

    public PokeEvent(long time, long selfId, long groupId, long userId, long targetId) {
        super(time, selfId, NoticeType.POKE);
        this.groupId = groupId;
        this.userId = userId;
        this.targetId = targetId;
    }
}
