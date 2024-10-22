package net.odinmc.core.common.scheduling;

import java.util.function.BiConsumer;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

record PromiseCompose<V, U>(@NotNull Promise<U> promise, @NotNull Function<V, ? extends Promise<U>> function, @NotNull V value, boolean sync)
    implements Runnable {
    @Override
    public void run() {
        if (this.promise.cancelled()) {
            return;
        }
        try {
            final var promise = this.function.apply(this.value);
            this.promise.setChild(promise);
            if (promise == null) {
                this.promise.complete(null);
            } else {
                final BiConsumer<U, Throwable> action = (u, throwable) -> {
                    if (throwable == null) {
                        this.promise.complete(u);
                    } else {
                        this.promise.completeExceptionally(throwable);
                    }
                };
                if (this.sync) {
                    promise.whenCompleteSync(action);
                } else {
                    promise.whenCompleteAsync(action);
                }
            }
        } catch (final PromiseFilterException filter) {
            this.promise.completeExceptionally(filter);
        } catch (final Throwable throwable) {
            Internal.logger().severe(throwable.getMessage(), throwable);
            this.promise.completeExceptionally(throwable);
        }
    }
}
