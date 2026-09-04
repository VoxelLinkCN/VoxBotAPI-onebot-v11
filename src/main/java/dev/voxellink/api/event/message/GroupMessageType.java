package dev.voxellink.api.event.message;

public enum GroupMessageType {
    NORMAL,
    ANONYMOUS,
    NOTICE;

    public static GroupMessageType from(String content) {
        return GroupMessageType.valueOf(content.toUpperCase());
    }
}
