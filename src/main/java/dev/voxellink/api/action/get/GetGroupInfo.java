package dev.voxellink.api.action.get;

import dev.voxellink.api.action.data.GroupInfo;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

import static dev.voxellink.api.util.VBotContentUtil.generateEcho;

@Data
@RequiredArgsConstructor
public class GetGroupInfo implements GetAction<GroupInfo> {
    private final long groupId;
    private final boolean noCache;
    private String echo;

    public GetGroupInfo(long groupId) {
        this.groupId = groupId;
        this.noCache = false;
    }

    @Override
    public GroupInfo parse(JSONObject jsonObject) {
        if (jsonObject.getString("status").equals("failed")) {
            return null;
        }
        jsonObject = jsonObject.getJSONObject("data");
        return new GroupInfo(
                jsonObject.getLong("group_id"),
                jsonObject.getString("group_name"),
                jsonObject.getInt("member_count"),
                jsonObject.getInt("max_member_count")
        );
    }

    @Override
    public JSONObject getData() {
        if (echo == null) {
            echo = generateEcho();
        }
        return new JSONObject()
                .put("action", "get_group_info")
                .put("params", new JSONObject()
                        .put("group_id", groupId)
                        .put("no_cache", noCache)
                )
                .put("echo", echo);
    }
}
