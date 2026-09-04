package dev.voxellink.api.action.set;

import dev.voxellink.api.action.Action;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

import static dev.voxellink.api.util.VBotContentUtil.generateEcho;

@Data
@RequiredArgsConstructor
public class SetFriendAddRequest implements Action {
    private final String flag;
    private final boolean approve;
    private final String remark;
    private String echo;

    public SetFriendAddRequest(String flag) {
        this(flag, true, "");
    }

    public SetFriendAddRequest(String flag, boolean approve) {
        this(flag, approve, "");
    }

    @Override
    public JSONObject getData() {
        if (echo == null) {
            echo = generateEcho();
        }
        return new JSONObject()
                .put("action", "set_friend_add_request")
                .put("params", new JSONObject()
                        .put("flag", flag)
                        .put("approve", approve)
                        .put("remark", remark))
                .put("echo", echo);
    }
}
