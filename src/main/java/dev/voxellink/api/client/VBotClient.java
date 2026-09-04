package dev.voxellink.api.client;

import dev.voxellink.api.action.Action;
import dev.voxellink.api.action.get.GetAction;
import dev.voxellink.api.listener.VBotListener;

import java.util.function.Consumer;

public interface VBotClient {
    void connect();

    void disconnect();

    void reconnect();

    void addListener(VBotListener listener);

    void removeListener(VBotListener listener);

    boolean hasListener(VBotListener listener);

    void action(Action action);

    <T> void action(GetAction<T> action, Consumer<T> consumer);

    boolean isConnected();
}
