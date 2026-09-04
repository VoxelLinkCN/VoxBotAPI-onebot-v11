package dev.voxellink.api.action.set;

import dev.voxellink.api.action.Action;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

import static dev.voxellink.api.util.VBotContentUtil.generateEcho;

@Data
@RequiredArgsConstructor
public class SendPrivateMessage implements Action {
    private final long userId;
    private final JSONArray message;
    private final boolean autoEscape;
    private String echo;

    public SendPrivateMessage(long userId, JSONArray message) {
        this.userId = userId;
        this.message = message;
        this.autoEscape = false;
    }

    @Override
    public JSONObject getData() {
        if (echo == null) {
            echo = generateEcho();
        }
        return new JSONObject()
                .put("action", "send_private_msg")
                .put("params", new JSONObject()
                        .put("user_id", userId)
                        .put("message", message)
                        .put("auto_escape", autoEscape))
                .put("echo", generateEcho());
    }
}
