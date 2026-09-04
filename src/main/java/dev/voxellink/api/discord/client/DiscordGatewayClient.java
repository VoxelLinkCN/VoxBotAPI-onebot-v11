package dev.voxellink.api.discord.client;

import dev.voxellink.api.discord.DiscordException;
import dev.voxellink.api.discord.DiscordSnowflake;
import dev.voxellink.api.discord.data.DiscordMessage;
import dev.voxellink.api.discord.event.DiscordEvent;
import dev.voxellink.api.discord.event.DiscordReadyEvent;
import dev.voxellink.api.discord.util.DiscordContentUtil;
import dev.voxellink.api.listener.VBotListener;
import dev.voxellink.api.util.VBotMethod;
import dev.voxellink.api.util.VBotReflectionUtil;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Discord Gateway v10 client with a small REST surface for channel messages.
 */
public class DiscordGatewayClient extends WebSocketClient implements DiscordClient {
    public static final int DEFAULT_INTENTS = 1 | (1 << 9) | (1 << 12) | (1 << 15);

    private static final Logger logger = LoggerFactory.getLogger(DiscordGatewayClient.class);
    public static final URI DEFAULT_GATEWAY = URI.create("wss://gateway.discord.gg/?v=10&encoding=json");
    public static final URI DEFAULT_API_BASE = URI.create("https://discord.com/api/v10");
    private static final int MAX_RATE_LIMIT_RETRIES = 3;
    private static final CloseableHttpClient HTTP_CLIENT = HttpClients.custom()
            .setDefaultRequestConfig(RequestConfig.custom()
                    .setConnectTimeout(10000)
                    .setConnectionRequestTimeout(10000)
                    .setSocketTimeout(15000)
                    .build())
            .disableAutomaticRetries()
            .build();

    private final String token;
    private final int intents;
    private final String apiBase;
    private final List<VBotMethod> methods = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "voxbotapi-discord-heartbeat");
        thread.setDaemon(true);
        return thread;
    });

    private volatile Integer sequence;
    private volatile long selfId;
    private volatile boolean heartbeatAcknowledged = true;
    private volatile ScheduledFuture<?> heartbeatTask;
    private volatile boolean manuallyClosed;
    private final AtomicBoolean reconnecting = new AtomicBoolean();

    public DiscordGatewayClient(String token) {
        this(token, DEFAULT_INTENTS);
    }

    public DiscordGatewayClient(String token, int intents) {
        this(DEFAULT_GATEWAY, DEFAULT_API_BASE, token, intents);
    }

    public DiscordGatewayClient(URI gateway, String token, int intents) {
        this(gateway, DEFAULT_API_BASE, token, intents);
    }

    public DiscordGatewayClient(URI gateway, URI apiBase, String token, int intents) {
        super(validateGateway(gateway));
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Discord bot token must not be empty");
        }
        this.token = token;
        this.intents = intents;
        this.apiBase = normalizeApiBase(apiBase);
        setConnectionLostTimeout(30);
    }

    private static URI validateGateway(URI gateway) {
        if (gateway == null || gateway.getScheme() == null
                || !("ws".equalsIgnoreCase(gateway.getScheme()) || "wss".equalsIgnoreCase(gateway.getScheme()))) {
            throw new IllegalArgumentException("Discord Gateway must use ws or wss");
        }
        return gateway;
    }

    private static String normalizeApiBase(URI apiBase) {
        if (apiBase == null || apiBase.getScheme() == null
                || !("http".equalsIgnoreCase(apiBase.getScheme()) || "https".equalsIgnoreCase(apiBase.getScheme()))) {
            throw new IllegalArgumentException("Discord API base must use http or https");
        }
        if (apiBase.getQuery() != null || apiBase.getFragment() != null) {
            throw new IllegalArgumentException("Discord API base must not contain a query or fragment");
        }
        String value = apiBase.toString();
        while (value.endsWith("/")) value = value.substring(0, value.length() - 1);
        return value;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        reconnecting.set(false);
        logger.info("Connected to Discord Gateway");
    }

    @Override
    public void onMessage(String content) {
        try {
            JSONObject payload = new JSONObject(content);
            int opcode = payload.getInt("op");
            if (!payload.isNull("s")) {
                sequence = payload.getInt("s");
            }
            switch (opcode) {
                case 0:
                    handleDispatch(payload.getString("t"), payload.getJSONObject("d"));
                    break;
                case 1:
                    sendHeartbeat();
                    break;
                case 7:
                    reconnectAsync();
                    break;
                case 9:
                    sequence = null;
                    reconnectAsync();
                    break;
                case 10:
                    startHeartbeat(payload.getJSONObject("d").getLong("heartbeat_interval"));
                    identify();
                    break;
                case 11:
                    heartbeatAcknowledged = true;
                    break;
                default:
                    break;
            }
        } catch (JSONException | IllegalStateException e) {
            logger.error("Failed to handle a Discord Gateway payload", e);
        }
    }

    private void identify() {
        JSONObject properties = new JSONObject()
                .put("os", System.getProperty("os.name", "unknown"))
                .put("browser", "VoxBotAPI")
                .put("device", "VoxBotAPI");
        JSONObject data = new JSONObject()
                .put("token", token)
                .put("intents", intents)
                .put("properties", properties);
        send(new JSONObject().put("op", 2).put("d", data).toString());
    }

    private synchronized void startHeartbeat(long intervalMillis) {
        cancelHeartbeat();
        heartbeatAcknowledged = true;
        long initialDelay = ThreadLocalRandom.current().nextLong(Math.max(1, intervalMillis));
        heartbeatTask = heartbeatExecutor.scheduleAtFixedRate(() -> {
            if (!heartbeatAcknowledged) {
                logger.warn("Discord heartbeat was not acknowledged; reconnecting");
                reconnectAsync();
                return;
            }
            sendHeartbeat();
        }, initialDelay, intervalMillis, TimeUnit.MILLISECONDS);
    }

    private void sendHeartbeat() {
        if (!isOpen()) return;
        heartbeatAcknowledged = false;
        Object value = sequence == null ? JSONObject.NULL : sequence;
        send(new JSONObject().put("op", 1).put("d", value).toString());
    }

    private void handleDispatch(String eventName, JSONObject data) {
        DiscordEvent event = DiscordContentUtil.parseDispatch(eventName, data, selfId);
        if (event instanceof DiscordReadyEvent) {
            selfId = event.getSelfId();
        }
        for (VBotMethod method : methods) {
            try {
                if (method.getEventClass() == null || method.getEventClass().isAssignableFrom(event.getClass())) {
                    if (method.getEventClass() == null) {
                        method.getMethod().invoke(method.getInstance());
                    } else {
                        method.getMethod().invoke(method.getInstance(), event);
                    }
                }
            } catch (Exception e) {
                logger.error("Failed to invoke Discord listener method", e);
            }
        }
    }

    private void reconnectAsync() {
        if (manuallyClosed || !reconnecting.compareAndSet(false, true)) return;
        CompletableFuture.runAsync(() -> {
            try {
                reconnectBlocking();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (RuntimeException e) {
                logger.error("Failed to reconnect to Discord Gateway", e);
            } finally {
                reconnecting.set(false);
            }
        });
    }

    private synchronized void cancelHeartbeat() {
        if (heartbeatTask != null) {
            heartbeatTask.cancel(false);
            heartbeatTask = null;
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        cancelHeartbeat();
        logger.info("Disconnected from Discord Gateway (code {})", code);
        if (!manuallyClosed && code != 4004 && code != 4010 && code != 4011 && code != 4012
                && code != 4013 && code != 4014) {
            reconnectAsync();
        }
    }

    @Override
    public void onError(Exception error) {
        logger.error("Discord client error", error);
    }

    @Override
    public void disconnect() {
        manuallyClosed = true;
        cancelHeartbeat();
        close();
    }

    @Override
    public void shutdown() {
        disconnect();
        heartbeatExecutor.shutdownNow();
    }

    @Override
    public void connect() {
        manuallyClosed = false;
        super.connect();
    }

    @Override
    public void reconnect() {
        manuallyClosed = false;
        super.reconnect();
    }

    @Override
    public boolean isConnected() {
        return isOpen();
    }

    @Override
    public void addListener(VBotListener listener) {
        methods.addAll(VBotReflectionUtil.getEventMethods(listener));
    }

    @Override
    public void removeListener(VBotListener listener) {
        methods.removeIf(method -> method.getInstance() == listener);
    }

    @Override
    public boolean hasListener(VBotListener listener) {
        return methods.stream().anyMatch(method -> method.getInstance() == listener);
    }

    @Override
    public CompletableFuture<DiscordMessage> sendMessage(long channelId, String content) {
        validateMessage(channelId, content);
        return request("POST", "/channels/" + channelId + "/messages", new JSONObject().put("content", content))
                .thenApply(DiscordMessage::from);
    }

    @Override
    public CompletableFuture<DiscordMessage> editMessage(long channelId, long messageId, String content) {
        validateMessage(channelId, content);
        DiscordSnowflake.requireValid(messageId);
        return request("PATCH", "/channels/" + channelId + "/messages/" + messageId, new JSONObject().put("content", content))
                .thenApply(DiscordMessage::from);
    }

    @Override
    public CompletableFuture<Void> deleteMessage(long channelId, long messageId) {
        DiscordSnowflake.requireValid(channelId);
        DiscordSnowflake.requireValid(messageId);
        return request("DELETE", "/channels/" + channelId + "/messages/" + messageId, null)
                .thenApply(ignored -> null);
    }

    private CompletableFuture<JSONObject> request(String method, String path, JSONObject body) {
        return CompletableFuture.supplyAsync(() -> {
            for (int attempt = 0; attempt <= MAX_RATE_LIMIT_RETRIES; attempt++) {
                HttpRequestBase request = createRequest(method, path, body);
                try (CloseableHttpResponse httpResponse = HTTP_CLIENT.execute(request)) {
                    int status = httpResponse.getStatusLine().getStatusCode();
                    String response = httpResponse.getEntity() == null ? ""
                            : EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
                    if (status == 429 && attempt < MAX_RATE_LIMIT_RETRIES) {
                        sleepForRateLimit(response);
                        continue;
                    }
                    if (status < 200 || status >= 300) {
                        throw new DiscordException(status, response.isEmpty() ? "Discord request failed" : response);
                    }
                    return response.isEmpty() ? new JSONObject() : new JSONObject(response);
                } catch (IOException e) {
                    throw new DiscordException(-1, "Discord REST request timed out or failed: "
                            + e.getClass().getSimpleName() + (e.getMessage() == null ? "" : ": " + e.getMessage()));
                } finally {
                    request.releaseConnection();
                }
            }
            throw new DiscordException(429, "Discord rate limit retry budget exhausted");
        });
    }

    private HttpRequestBase createRequest(String method, String path, JSONObject body) {
        HttpRequestBase request;
        if ("POST".equals(method)) request = new HttpPost(apiBase + path);
        else if ("PATCH".equals(method)) request = new HttpPatch(apiBase + path);
        else if ("DELETE".equals(method)) request = new HttpDelete(apiBase + path);
        else throw new IllegalArgumentException("Unsupported Discord REST method: " + method);
        request.setHeader("Authorization", "Bot " + token);
        request.setHeader("User-Agent", "DiscordBot (https://github.com/voxellink/VoxBotAPI-onebot-v11, 1.0)");
        if (body != null) {
            ((HttpEntityEnclosingRequestBase) request).setEntity(
                    new StringEntity(body.toString(), ContentType.APPLICATION_JSON));
        }
        return request;
    }

    private static void sleepForRateLimit(String response) {
        double retrySeconds = 1;
        try {
            retrySeconds = new JSONObject(response).optDouble("retry_after", 1);
        } catch (JSONException ignored) {
        }
        long delayMillis = Math.min(60000, Math.max(0, (long) Math.ceil(retrySeconds * 1000)));
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DiscordException(429, "Interrupted while waiting for Discord rate limit");
        }
    }

    private static void validateMessage(long channelId, String content) {
        DiscordSnowflake.requireValid(channelId);
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Discord message content must not be empty");
        }
        if (content.length() > 2000) {
            throw new IllegalArgumentException("Discord message content must not exceed 2000 characters");
        }
    }

    public URI getApiBase() {
        return URI.create(apiBase);
    }
}
