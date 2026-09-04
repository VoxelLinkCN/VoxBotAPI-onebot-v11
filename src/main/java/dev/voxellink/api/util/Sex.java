package dev.voxellink.api.util;

public enum Sex {
    MALE,
    FEMALE,
    UNKNOWN;

    public static Sex from(String sex) {
        sex = sex.toLowerCase();
        switch (sex) {
            case "male":
                return MALE;
            case "female":
                return FEMALE;
            default:
                return UNKNOWN;
        }
    }
}
