package dev.voxellink.api.event.message;

public enum PrivateMessageType {
    FRIEND,
    GROUP,
    OTHER;

    public static PrivateMessageType from(String type) {
        type = type.toLowerCase();
        switch (type) {
            case "friend":
                return FRIEND;
            case "group":
                return GROUP;
            default:
                return OTHER;
        }
    }
}
