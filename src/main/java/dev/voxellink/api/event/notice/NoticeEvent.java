package dev.voxellink.api.event.notice;

import dev.voxellink.api.event.NEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class NoticeEvent extends NEvent {
    private final NoticeType noticeType;

    protected NoticeEvent(long time, long selfId, NoticeType noticeType) {
        super(time, selfId);
        this.noticeType = noticeType;
    }
}
