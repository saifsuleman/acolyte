package net.odinmc.core.common.scheduling;

import org.jetbrains.annotations.NotNull;

record UncheckedRunnable(@NotNull Runnable delegate) implements Runnable {
    @Override
    public void run() {
        try {
            this.delegate.run();
        } catch (final PromiseFilterException ignored) {} catch (final Throwable throwable) {
            Internal.logger().severe(throwable.getMessage(), throwable);
        }
    }
}
