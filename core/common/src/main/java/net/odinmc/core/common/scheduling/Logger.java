package net.odinmc.core.common.scheduling;

import org.jetbrains.annotations.NotNull;

public interface Logger {
    void severe(@NotNull String message, @NotNull Throwable cause);
}
