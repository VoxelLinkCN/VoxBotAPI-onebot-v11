package dev.voxellink.api.event.notice;

public enum GroupDecreaseType {
    LEAVE,
    KICK,
    KICK_ME;

    public static GroupDecreaseType from(String type) {
        switch (type) {
            case "leave":
                return LEAVE;
            case "kick":
                return KICK;
            case "kick_me":
                return KICK_ME;
            default:
                return null;
        }
    }
}
