package dev.voxellink.api.util;

import dev.voxellink.api.event.NEvent;
import dev.voxellink.api.listener.VBotListener;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

@Data
public class VBotMethod {
    private final VBotListener instance;
    private final Method method;
    private final @Nullable Class<? extends NEvent> eventClass;
}
