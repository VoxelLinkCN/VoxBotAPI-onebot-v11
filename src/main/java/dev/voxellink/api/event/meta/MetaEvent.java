package dev.voxellink.api.event.meta;

import dev.voxellink.api.event.NEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class MetaEvent extends NEvent {
    private final MetaEventType metaEventType;

    protected MetaEvent(long time, long selfId, MetaEventType metaEventType) {
        super(time, selfId);
        this.metaEventType = metaEventType;
    }
}
