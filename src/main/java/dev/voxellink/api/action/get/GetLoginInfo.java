package dev.voxellink.api.action.get;

import dev.voxellink.api.action.data.BasicInfo;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

import static dev.voxellink.api.util.VBotContentUtil.generateEcho;

@Data
@RequiredArgsConstructor
public class GetLoginInfo implements GetAction<BasicInfo> {
    private String echo;

    @Override
    public BasicInfo parse(JSONObject jsonObject) {
        if (jsonObject.getString("status").equals("failed")) {
            return null;
        }
        jsonObject = jsonObject.getJSONObject("data");
        return new BasicInfo(
                jsonObject.getLong("user_id"),
                jsonObject.getString("nickname")
        );
    }

    @Override
    public JSONObject getData() {
        if (echo == null) {
            echo = generateEcho();
        }
        return new JSONObject()
                .put("action", "get_login_info")
                .put("params", new JSONObject())
                .put("echo", echo);
    }
}
