package dev.voxellink.api.action.set;

import dev.voxellink.api.action.Action;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

import static dev.voxellink.api.util.VBotContentUtil.generateEcho;

@Data
@RequiredArgsConstructor
public class SetGroupSpecialTitle implements Action {
    private final long groupId;
    private final long userId;
    private final String specialTitle;
    private long duration = -1;
    private String echo;

    public SetGroupSpecialTitle(long groupId, long userId, String specialTitle, long duration) {
        this.groupId = groupId;
        this.userId = userId;
        this.specialTitle = specialTitle;
        this.duration = duration;
    }

    @Override
    public JSONObject getData() {
        if (echo == null) {
            echo = generateEcho();
        }
        return new JSONObject()
                .put("action", "set_group_special_title")
                .put("params", new JSONObject()
                        .put("group_id", groupId)
                        .put("user_id", userId)
                        .put("special_title", specialTitle)
                        .put("duration", duration)
                )
                .put("echo", echo);
    }
}
