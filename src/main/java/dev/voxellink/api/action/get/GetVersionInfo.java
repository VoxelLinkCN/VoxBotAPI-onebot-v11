package dev.voxellink.api.action.get;

import dev.voxellink.api.action.data.VersionInfo;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import static dev.voxellink.api.util.VBotContentUtil.generateEcho;

@Data
@RequiredArgsConstructor
public class GetVersionInfo implements GetAction<VersionInfo> {
    private String echo;

    @Override
    public @Nullable VersionInfo parse(JSONObject jsonObject) {
        if (jsonObject.getString("status").equals("failed")) {
            return null;
        }
        jsonObject = jsonObject.getJSONObject("data");
        return new VersionInfo(
                jsonObject.getString("app_name"),
                jsonObject.getString("app_version"),
                jsonObject.getString("protocol_version")
        );
    }

    @Override
    public JSONObject getData() {
        if (echo == null) {
            echo = generateEcho();
        }
        return new JSONObject()
                .put("action", "get_version_info")
                .put("params", new JSONObject())
                .put("echo", echo);
    }
}
