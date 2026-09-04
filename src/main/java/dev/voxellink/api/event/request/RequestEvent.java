package dev.voxellink.api.event.request;

import dev.voxellink.api.event.NEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class RequestEvent extends NEvent {
    private final RequestType requestType;
    private final long userId;
    private final String comment;
    private final String flag;

    protected RequestEvent(long time, long selfId, RequestType requestType, long userId, String comment, String flag) {
        super(time, selfId);
        this.requestType = requestType;
        this.userId = userId;
        this.comment = comment;
        this.flag = flag;
    }
}
