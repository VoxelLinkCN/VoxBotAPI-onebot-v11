package dev.voxellink.api.event.meta;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class LifecycleEvent extends MetaEvent {
    private final boolean connected;

    public LifecycleEvent(long time, long selfId, boolean connected) {
        super(time, selfId, MetaEventType.LIFECYCLE);
        this.connected = connected;
    }
}
