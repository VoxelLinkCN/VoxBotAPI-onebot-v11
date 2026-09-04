package dev.voxellink.api.action.set;

import dev.voxellink.api.action.Action;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

import static dev.voxellink.api.util.VBotContentUtil.generateEcho;

@Data
@RequiredArgsConstructor
public class SetGroupKick implements Action {
    private final long groupId;
    private final long userId;
    private final boolean rejectAddRequest;
    private String echo;

    public SetGroupKick(long groupId, long userId) {
        this.groupId = groupId;
        this.userId = userId;
        this.rejectAddRequest = false;
    }

    @Override
    public JSONObject getData() {
        if (echo == null) {
            echo = generateEcho();
        }
        return new JSONObject()
                .put("action", "set_group_kick")
                .put("params", new JSONObject()
                        .put("group_id", groupId)
                        .put("user_id", userId)
                        .put("reject_add_request", rejectAddRequest)
                )
                .put("echo", echo);
    }
}
