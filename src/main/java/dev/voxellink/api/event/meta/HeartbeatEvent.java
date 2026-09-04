package dev.voxellink.api.event.meta;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class HeartbeatEvent extends MetaEvent {
    private final boolean good;
    private final boolean online;
    private final long interval;

    public HeartbeatEvent(long time, long selfId, boolean good, boolean online, long interval) {
        super(time, selfId, MetaEventType.HEARTBEAT);
        this.good = good;
        this.online = online;
        this.interval = interval;
    }
}
