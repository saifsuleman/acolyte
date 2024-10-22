package net.odinmc.core.common.scheduling;

import java.util.concurrent.Callable;
import org.jetbrains.annotations.NotNull;

record PromiseThrowingSupply<V>(@NotNull Promise<V> promise, @NotNull Callable<V> supplier) implements Runnable {
    @Override
    public void run() {
        if (this.promise.cancelled()) {
            return;
        }
        try {
            this.promise.complete(this.supplier.call());
        } catch (final PromiseFilterException filter) {
            this.promise.completeExceptionally(filter);
        } catch (final Throwable throwable) {
            Internal.logger().severe(throwable.getMessage(), throwable);
            this.promise.completeExceptionally(throwable);
        }
    }
}
