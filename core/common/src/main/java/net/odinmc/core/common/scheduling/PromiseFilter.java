package net.odinmc.core.common.scheduling;

import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

record PromiseFilter<V>(@NotNull Promise<V> promise, @NotNull Predicate<V> filter, @NotNull V value) implements Runnable {
    @Override
    public void run() {
        if (this.promise.cancelled()) {
            return;
        }
        try {
            if (this.filter.test(this.value)) {
                this.promise.complete(this.value);
            } else {
                this.promise.completeExceptionally(new PromiseFilterException());
            }
        } catch (final PromiseFilterException filter) {
            this.promise.completeExceptionally(filter);
        } catch (final Throwable throwable) {
            Internal.logger().severe(throwable.getMessage(), throwable);
            this.promise.completeExceptionally(throwable);
        }
    }
}
