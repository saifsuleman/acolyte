package net.odinmc.core.common.scheduling;

import org.jetbrains.annotations.NotNull;

public enum ThreadContext {
    SYNC,

    ASYNC;

    @NotNull
    public static ThreadContext forCurrentThread() {
        return ThreadContext.forThread(Thread.currentThread());
    }

    @NotNull
    public static ThreadContext forThread(@NotNull final Thread thread) {
        return Internal.mainThread().equals(thread) ? ThreadContext.SYNC : ThreadContext.ASYNC;
    }
}
