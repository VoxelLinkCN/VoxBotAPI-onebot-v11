package dev.voxellink.api.action.data;

import dev.voxellink.api.util.Sex;
import lombok.Data;

@Data
public class StrangerInfo {
    private final long userId;
    private final String nickname;
    private final Sex sex;
    private final int age;
}
