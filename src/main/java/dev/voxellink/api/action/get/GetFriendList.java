package dev.voxellink.api.action.get;

import dev.voxellink.api.action.data.FriendInfo;
import dev.voxellink.api.action.data.FriendList;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

import static dev.voxellink.api.util.VBotContentUtil.generateEcho;

@Data
@RequiredArgsConstructor
public class GetFriendList implements GetAction<FriendList> {
    private String echo;

    @Override
    public FriendList parse(JSONObject jsonObject) {
        if (jsonObject.getString("status").equals("failed")) {
            return null;
        }
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        FriendList list = new FriendList();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            FriendInfo friendInfo = new FriendInfo(
                    object.getLong("user_id"),
                    object.getString("nickname"),
                    object.getString("remark")
            );
            list.add(friendInfo);
        }
        return list;
    }

    @Override
    public JSONObject getData() {
        if (echo == null) {
            echo = generateEcho();
        }
        return new JSONObject()
                .put("action", "get_friend_list")
                .put("params", new JSONObject())
                .put("echo", echo);
    }
}
