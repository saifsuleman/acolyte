package net.odinmc.core.common.scheduling;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public record PromiseSupply<V>(@NotNull Promise<V> promise, @NotNull Supplier<V> supplier) implements Runnable {
    @Override
    public void run() {
        if (this.promise.cancelled()) {
            return;
        }
        try {
            this.promise.complete(this.supplier.get());
        } catch (final PromiseFilterException filter) {
            this.promise.completeExceptionally(filter);
        } catch (final Throwable throwable) {
            Internal.logger().severe(throwable.getMessage(), throwable);
            this.promise.completeExceptionally(throwable);
        }
    }
}
