package dev.voxellink.api.action.set;

import dev.voxellink.api.action.Action;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

import static dev.voxellink.api.util.VBotContentUtil.generateEcho;

@Data
@RequiredArgsConstructor
public class SetRestart implements Action {
    private final int delay;
    private String echo;

    public SetRestart() {
        this(0);
    }

    @Override
    public JSONObject getData() {
        if (echo == null) {
            echo = generateEcho();
        }
        return new JSONObject()
                .put("action", "set_restart")
                .put("params", new JSONObject()
                        .put("delay", delay))
                .put("echo", echo);
    }
}
