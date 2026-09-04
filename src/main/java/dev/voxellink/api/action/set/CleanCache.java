package dev.voxellink.api.action.set;

import dev.voxellink.api.action.Action;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

import static dev.voxellink.api.util.VBotContentUtil.generateEcho;

@Data
@RequiredArgsConstructor
public class CleanCache implements Action {
    private String echo;

    @Override
    public JSONObject getData() {
        if (echo == null) {
            echo = generateEcho();
        }
        return new JSONObject()
                .put("action", "clean_cache")
                .put("params", new JSONObject())
                .put("echo", echo);
    }
}
