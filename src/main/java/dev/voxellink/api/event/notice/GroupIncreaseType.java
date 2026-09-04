package dev.voxellink.api.event.notice;

public enum GroupIncreaseType {
    INVITE,
    APPROVE;

    public static GroupIncreaseType from(String type) {
        switch (type) {
            case "invite":
                return INVITE;
            case "approve":
                return APPROVE;
            default:
                return null;
        }
    }
}
