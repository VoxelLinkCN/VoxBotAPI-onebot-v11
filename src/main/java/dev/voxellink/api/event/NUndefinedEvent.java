package dev.voxellink.api.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONObject;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class NUndefinedEvent extends NEvent {
    private final JSONObject data;

    public NUndefinedEvent(long time, long selfId, JSONObject data) {
        super(time, selfId);
        this.data = data;
    }
}
