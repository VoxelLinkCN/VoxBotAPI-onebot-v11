package dev.voxellink.api.action.get;

import dev.voxellink.api.action.data.GroupInfo;
import dev.voxellink.api.action.data.GroupList;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import static dev.voxellink.api.util.VBotContentUtil.generateEcho;

@Data
@RequiredArgsConstructor
public class GetGroupList implements GetAction<GroupList> {
    private String echo;

    @Override
    public @Nullable GroupList parse(JSONObject jsonObject) {
        if (jsonObject.getString("status").equals("failed")) {
            return null;
        }
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        GroupList list = new GroupList();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            list.add(new GroupInfo(
                    jsonObject1.getLong("group_id"),
                    jsonObject1.getString("group_name"),
                    jsonObject1.getInt("member_count"),
                    jsonObject1.getInt("max_member_count")));
        }
        return list;
    }

    @Override
    public JSONObject getData() {
        if (echo == null) {
            echo = generateEcho();
        }
        return new JSONObject()
                .put("action", "get_group_list")
                .put("params", new JSONObject())
                .put("echo", echo);
    }
}
