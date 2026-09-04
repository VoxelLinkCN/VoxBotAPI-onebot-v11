package dev.voxellink.api.util;

import dev.voxellink.api.event.NEvent;
import dev.voxellink.api.listener.VBotListener;
import dev.voxellink.api.listener.VBotEventHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class VBotReflectionUtil {
    public static List<VBotMethod> getEventMethods(VBotListener instance) {
        List<VBotMethod> set = new ArrayList<>();
        for (Method method : instance.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(VBotEventHandler.class)) {
                continue;
            }
            if (method.getParameterCount() > 1) {
                continue;
            } else if (method.getParameterCount() == 0) {
                set.add(new VBotMethod(instance, method, NEvent.class));
                method.setAccessible(true);
            } else {
                Class<?> parameterType = method.getParameterTypes()[0];
                if (!NEvent.class.isAssignableFrom(parameterType)) {
                    continue;
                }
                set.add(new VBotMethod(instance, method, (Class<? extends NEvent>) parameterType));
                method.setAccessible(true);
            }
        }
        return set;
    }
}
