package net.odinmc.core.common.scheduling;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

record PromiseApply<V, U>(@NotNull Promise<U> promise, @NotNull Function<V, ? extends U> function, @NotNull V value) implements Runnable {
    @Override
    public void run() {
        if (this.promise.cancelled()) {
            return;
        }
        try {
            this.promise.complete(this.function.apply(this.value));
        } catch (final PromiseFilterException filter) {
            this.promise.completeExceptionally(filter);
        } catch (final Throwable throwable) {
            Internal.logger().severe(throwable.getMessage(), throwable);
            this.promise.completeExceptionally(throwable);
        }
    }
}
