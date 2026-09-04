package dev.voxellink.api.action.set;

import dev.voxellink.api.action.Action;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

import static dev.voxellink.api.util.VBotContentUtil.generateEcho;

@Data
@RequiredArgsConstructor
public class SendGroupMessage implements Action {
    private final long groupId;
    private final JSONArray message;
    private final boolean autoEscape;
    private String echo;

    public SendGroupMessage(long groupId, JSONArray message) {
        this.groupId = groupId;
        this.message = message;
        this.autoEscape = false;
    }

    @Override
    public JSONObject getData() {
        if (echo == null) {
            echo = generateEcho();
        }
        return new JSONObject()
                .put("action", "send_group_msg")
                .put("params", new JSONObject()
                        .put("group_id", groupId)
                        .put("message", message)
                        .put("auto_escape", autoEscape))
                .put("echo", generateEcho());
    }
}
