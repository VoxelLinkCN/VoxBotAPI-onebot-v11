package dev.voxellink.api.action.get;

import dev.voxellink.api.action.Action;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public interface GetAction<T> extends Action {
    @Nullable
    T parse(JSONObject jsonObject);
}
