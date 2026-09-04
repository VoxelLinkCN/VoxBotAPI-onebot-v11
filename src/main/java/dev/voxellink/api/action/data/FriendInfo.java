package dev.voxellink.api.action.data;

import lombok.Data;

@Data
public class FriendInfo {
    private final long userId;
    private final String nickname;
    private final String remark;
}
