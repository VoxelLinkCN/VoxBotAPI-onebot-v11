package dev.voxellink.api.action.get;

import dev.voxellink.api.action.data.BasicInfo;
import dev.voxellink.api.action.data.GroupMemberList;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import static dev.voxellink.api.util.VBotContentUtil.generateEcho;

@Data
@RequiredArgsConstructor
public class GetGroupMemberList implements GetAction<GroupMemberList> {
    private final long groupId;
    private String echo;

    @Override
    public @Nullable GroupMemberList parse(JSONObject jsonObject) {
        if (jsonObject.getString("status").equals("failed")) {
            return null;
        }
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        GroupMemberList list = new GroupMemberList();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            list.add(new BasicInfo(
                    object.getLong("user_id"),
                    object.getString("nickname")
            ));
        }
        return list;
    }

    @Override
    public JSONObject getData() {
        if (echo == null) {
            echo = generateEcho();
        }
        return new JSONObject()
                .put("action", "get_group_member_list")
                .put("params", new JSONObject()
                        .put("group_id", groupId)
                )
                .put("echo", echo);
    }
}
