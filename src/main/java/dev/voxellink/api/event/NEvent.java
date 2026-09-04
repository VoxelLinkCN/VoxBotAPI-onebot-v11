package dev.voxellink.api.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class NEvent {
    private final long time;
    private final long selfId;

    protected NEvent(long time, long selfId) {
        this.time = time;
        this.selfId = selfId;
    }

}
