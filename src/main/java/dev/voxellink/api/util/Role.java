package dev.voxellink.api.util;

public enum Role {
    OWNER,
    ADMIN,
    MEMBER;

    public static Role from(String role) {
        switch (role) {
            case "owner":
                return OWNER;
            case "admin":
                return ADMIN;
            case "member":
                return MEMBER;
            default:
                return null;
        }
    }
}
