package dev.voxellink.api.event.notice;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class FriendAddEvent extends NoticeEvent {
    private final long userId;

    public FriendAddEvent(long time, long selfId, long userId) {
        super(time, selfId, NoticeType.FRIEND_ADD);
        this.userId = userId;
    }
}
