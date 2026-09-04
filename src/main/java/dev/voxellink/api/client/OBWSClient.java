package dev.voxellink.api.client;

import dev.voxellink.api.action.Action;
import dev.voxellink.api.action.get.GetAction;
import dev.voxellink.api.event.NEvent;
import dev.voxellink.api.listener.VBotListener;
import dev.voxellink.api.util.VBotContentUtil;
import dev.voxellink.api.util.VBotMapUtil;
import dev.voxellink.api.util.VBotMethod;
import dev.voxellink.api.util.VBotReflectionUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class OBWSClient extends WebSocketClient implements VBotClient {
    private static final Logger logger = LoggerFactory.getLogger(OBWSClient.class);

    private final List<VBotMethod> methods = new ArrayList<>();

    private final Map<GetAction<?>, Consumer<?>> consumerMap = new HashMap<>();

    private boolean isConnected = false;

    public OBWSClient(String address, int port) throws URISyntaxException {
        super(new URI("ws://" + address + ":" + port));
        setConnectionLostTimeout(30);
    }

    public OBWSClient(String address, int port, @Nullable String accessToken) throws URISyntaxException {
        super(new URI("ws://" + address + ":" + port), VBotMapUtil.of("Authorization", "Bearer " + accessToken));
        setConnectionLostTimeout(30);
    }

    public OBWSClient(URI url, @Nullable String accessToken) {
        super(url, VBotMapUtil.of("Authorization", "Bearer " + accessToken));
        setConnectionLostTimeout(30);
    }

    public OBWSClient(URI url) {
        super(url);
        setConnectionLostTimeout(30);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        isConnected = true;
    }

    @Override
    public void onMessage(String s) {
        NEvent event = VBotContentUtil.onebot11Parse(s);
        if (event != null) {
            for (VBotMethod method : methods) {
                try {
                    if (method.getEventClass() == null) {
                        method.getMethod().invoke(method.getInstance());
                        continue;
                    }
                    if (method.getEventClass().isAssignableFrom(event.getClass())) {
                        method.getMethod().invoke(method.getInstance(), event);
                    }
                } catch (Exception e) {
                    logger.error("Failed to invoke listener class method", e);
                }
            }
        }
        JSONObject jsonObject = new JSONObject(s);
        if (jsonObject.has("echo")) {
            for (GetAction<?> getAction : consumerMap.keySet()) {
                String echo = getAction.getData().getString("echo");
                if (echo.equals(jsonObject.getString("echo"))) {
                    Consumer<Object> consumer = (Consumer<Object>) consumerMap.get(getAction);
                    consumer.accept(getAction.parse(jsonObject));
                }
            }
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        isConnected = false;
    }

    @Override
    public void onError(Exception e) {
        logger.error("There's a error occurred in WebsocketClient", e);
        isConnected = false;
    }

    @Override
    public void disconnect() {
        super.close();
    }

    @Override
    public void addListener(VBotListener listener) {
        methods.addAll(VBotReflectionUtil.getEventMethods(listener));
    }

    @Override
    public void removeListener(VBotListener listener) {
        methods.removeAll(VBotReflectionUtil.getEventMethods(listener));
    }

    @Override
    public boolean hasListener(VBotListener listener) {
        return methods.stream().map(VBotMethod::getEventClass).collect(Collectors.toList()).contains(listener.getClass());
    }

    @Override
    public void action(Action action) {
        send(action.getData().toString());
    }

    @Override
    public <T> void action(GetAction<T> action, Consumer<T> consumer) {
        send(action.getData().toString());
        consumerMap.put(action, consumer);
    }
}
