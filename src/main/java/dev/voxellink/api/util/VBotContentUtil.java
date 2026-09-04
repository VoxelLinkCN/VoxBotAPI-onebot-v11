package dev.voxellink.api.util;

import dev.voxellink.api.event.NEvent;
import dev.voxellink.api.event.NUndefinedEvent;
import dev.voxellink.api.event.message.GroupMessageType;
import dev.voxellink.api.event.message.GroupMessageEvent;
import dev.voxellink.api.event.message.PrivateMessageEvent;
import dev.voxellink.api.event.message.PrivateMessageType;
import dev.voxellink.api.event.meta.HeartbeatEvent;
import dev.voxellink.api.event.meta.LifecycleEvent;
import dev.voxellink.api.event.notice.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Random;

public class VBotContentUtil {
    private static final Logger logger = LoggerFactory.getLogger(VBotContentUtil.class);

    public static String generateEcho() {
        Random random = new Random(new Random().nextInt());
        return "voxbotapi_" + random.nextInt(10000);
    }

    public static NEvent onebot11Parse(String content) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(content);
        } catch (JSONException e) {
            logger.error("Failed to parse content with OneBot 11 parser", e);
            return null;
        }
        if (!jsonObject.has("post_type")) return null;
        long time = jsonObject.getLong("time");
        long selfId = jsonObject.getLong("self_id");
        String postType = jsonObject.getString("post_type");
        if (postType.equalsIgnoreCase("message")) {
            String messageType = jsonObject.getString("message_type");
            int messageId = jsonObject.getInt("message_id");
            long senderId = jsonObject.getLong("user_id");
            JSONArray message = jsonObject.getJSONArray("message");
            String rawMessage = jsonObject.getString("raw_message");
            if (messageType.equalsIgnoreCase("group")) {
                GroupMessageType type = GroupMessageType.from(jsonObject.getString("sub_type"));
                long groupId = jsonObject.getLong("group_id");
                if (type == GroupMessageType.ANONYMOUS) {
                    JSONObject anonymous = jsonObject.getJSONObject("anonymous");
                    long anonymousId = anonymous.getLong("id");
                    String anonymousName = anonymous.getString("name");
                    return new GroupMessageEvent(time, selfId, messageId, senderId, message, rawMessage, type, groupId, anonymousId, anonymousName);
                } else {
                    return new GroupMessageEvent(time, selfId, messageId, senderId, message, rawMessage, type, groupId, -1L, null);
                }
            } else if (messageType.equalsIgnoreCase("private")) {
                PrivateMessageType type = PrivateMessageType.from(jsonObject.getString("sub_type"));
                return new PrivateMessageEvent(time, selfId, messageId, senderId, message, rawMessage, type);
            }
        } else if (postType.equalsIgnoreCase("meta_event")) {
            String metaEventType = jsonObject.getString("meta_event_type");
            if (metaEventType.equalsIgnoreCase("heartbeat")) {
                long interval = jsonObject.getLong("interval");
                JSONObject status = jsonObject.getJSONObject("status");
                boolean good = status.getBoolean("good");
                boolean online = status.getBoolean("online");
                return new HeartbeatEvent(time, selfId, good, online, interval);
            } else if (metaEventType.equalsIgnoreCase("lifecycle")) {
                String subType = jsonObject.getString("sub_type");
                return new LifecycleEvent(time, selfId, Objects.equals(subType, "connect"));
            }
        } else if (postType.equalsIgnoreCase("notice")) {
            String noticeType = jsonObject.getString("notice_type");
            if (noticeType.equalsIgnoreCase("group_decrease")) {
                GroupDecreaseType subType = GroupDecreaseType.from(jsonObject.getString("sub_type"));
                long groupId = jsonObject.getLong("group_id");
                long operatorId = jsonObject.getLong("operator_id");
                long userId = jsonObject.getLong("user_id");
                return new GroupDecreaseEvent(time, selfId, subType, groupId, operatorId, userId);
            } else if (noticeType.equalsIgnoreCase("group_increase")) {
                GroupIncreaseType subType = GroupIncreaseType.from(jsonObject.getString("sub_type"));
                long groupId = jsonObject.getLong("group_id");
                long operatorId = jsonObject.getLong("operator_id");
                long userId = jsonObject.getLong("user_id");
                return new GroupIncreaseEvent(time, selfId, subType, groupId, operatorId, userId);
            } else if (noticeType.equalsIgnoreCase("friend_add")) {
                long userId = jsonObject.getLong("user_id");
                return new FriendAddEvent(time, selfId, userId);
            } else if (noticeType.equalsIgnoreCase("notify")) {
                long groupId = jsonObject.getLong("group_id");
                long userId = jsonObject.getLong("user_id");
                long targetId = jsonObject.getLong("target_id");
                return new PokeEvent(time, selfId, groupId, userId, targetId);
            }
        }
        return new NUndefinedEvent(time, selfId, jsonObject);
    }
}
