package dev.voxellink.api.action.get;

import dev.voxellink.api.action.data.GroupMemberInfo;
import dev.voxellink.api.util.Role;
import dev.voxellink.api.util.Sex;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import static dev.voxellink.api.util.VBotContentUtil.generateEcho;

@Data
@RequiredArgsConstructor
public class GetGroupMemberInfo implements GetAction<GroupMemberInfo> {
    private final long groupId;
    private final long userId;
    private final boolean noCache;
    private String echo;

    public GetGroupMemberInfo(long groupId, long userId) {
        this.groupId = groupId;
        this.userId = userId;
        this.noCache = false;
    }

    @Override
    public @Nullable GroupMemberInfo parse(JSONObject jsonObject) {
        if (jsonObject.getString("status").equals("failed")) {
            return null;
        }
        jsonObject = jsonObject.getJSONObject("data");
        return new GroupMemberInfo(
                jsonObject.getLong("group_id"),
                jsonObject.getLong("user_id"),
                jsonObject.getString("nickname"),
                jsonObject.getString("card"),
                Sex.from(jsonObject.getString("sex")),
                jsonObject.getInt("age"),
                jsonObject.getString("area"),
                jsonObject.getInt("join_time"),
                jsonObject.getInt("last_sent_time"),
                jsonObject.getString("level"),
                Role.from(jsonObject.getString("role")),
                jsonObject.getBoolean("unfriendly"),
                jsonObject.getString("title"),
                jsonObject.getInt("title_expire_time"),
                jsonObject.getBoolean("card_changeable")
        );
    }

    @Override
    public JSONObject getData() {
        if (echo == null) {
            echo = generateEcho();
        }
        return new JSONObject()
                .put("action", "get_group_member_info")
                .put("params", new JSONObject()
                        .put("group_id", groupId)
                        .put("user_id", userId)
                        .put("no_cache", noCache))
                .put("echo", echo);
    }
}
