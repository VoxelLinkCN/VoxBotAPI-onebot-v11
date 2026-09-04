package dev.voxellink.api.event.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class FriendRequestEvent extends RequestEvent {
    public FriendRequestEvent(long time, long selfId, long userId, String comment, String flag) {
        super(time, selfId, RequestType.FRIEND, userId, comment, flag);
    }
}
