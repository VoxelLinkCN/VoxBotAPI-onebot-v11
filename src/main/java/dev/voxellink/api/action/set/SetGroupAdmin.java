package dev.voxellink.api.action.set;

import dev.voxellink.api.action.Action;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

import static dev.voxellink.api.util.VBotContentUtil.generateEcho;

@Data
@RequiredArgsConstructor
public class SetGroupAdmin implements Action {
    private final long groupId;
    private final long userId;
    private boolean enable = false;
    private String echo;

    public SetGroupAdmin(long groupId, long userId, boolean enable) {
        this.groupId = groupId;
        this.userId = userId;
        this.enable = enable;
    }

    @Override
    public JSONObject getData() {
        if (echo == null) {
            echo = generateEcho();
        }
        return new JSONObject()
                .put("action", "set_group_admin")
                .put("params", new JSONObject()
                        .put("group_id", groupId)
                        .put("user_id", userId)
                        .put("enable", enable)
                )
                .put("echo", echo);
    }
}
