package net.odinmc.core.common.scheduling;

import java.util.function.BiFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record PromiseHandle<V, U>(
    @NotNull Promise<U> promise,
    @NotNull BiFunction<V, Throwable, ? extends U> function,
    @Nullable V value,
    @Nullable Throwable throwable
)
    implements Runnable {
    @Override
    public void run() {
        if (this.promise.cancelled()) {
            return;
        }
        try {
            this.promise.complete(this.function.apply(this.value, this.throwable));
        } catch (final PromiseFilterException filter) {
            this.promise.completeExceptionally(filter);
        } catch (final Throwable throwable) {
            Internal.logger().severe(throwable.getMessage(), throwable);
            this.promise.completeExceptionally(throwable);
        }
    }
}
