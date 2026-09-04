package dev.voxellink.api;

import dev.voxellink.api.action.get.GetGroupInfo;
import dev.voxellink.api.action.set.SendGroupMessage;
import dev.voxellink.api.client.OBWSClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import java.net.URISyntaxException;

@Disabled("Integration tests require a OneBot 11 websocket server on 127.0.0.1:3001")
public class VoxBotTest {
    @Test
    public void actionTest() throws URISyntaxException, InterruptedException {
        OBWSClient client = new OBWSClient("127.0.0.1", 3001, "");
        client.connect();
        Thread.sleep(100);
        JSONArray messages = new JSONArray()
                .put(new JSONObject()
                        .put("type", "text")
                        .put("data", new JSONObject()
                                .put("text", "测试")));
        SendGroupMessage message = new SendGroupMessage(721509831L, messages);
        client.action(message);
        client.disconnect();
    }

    @Test
    public void getActionTest() throws URISyntaxException, InterruptedException {
        OBWSClient client = new OBWSClient("127.0.0.1", 3001, "");
        client.connect();
        Thread.sleep(100);
        GetGroupInfo groupInfo = new GetGroupInfo(721509831L);
        client.action(groupInfo, System.out::println);
        Thread.sleep(1000);
        client.disconnect();
    }
}
