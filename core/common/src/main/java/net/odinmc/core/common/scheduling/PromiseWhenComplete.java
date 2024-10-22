package net.odinmc.core.common.scheduling;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record PromiseWhenComplete<U>(
    @NotNull Promise<U> promise,
    @NotNull BiConsumer<U, Throwable> consumer,
    @Nullable U value,
    @Nullable Throwable throwable
)
    implements Runnable {
    @Override
    public void run() {
        if (this.promise.cancelled()) {
            return;
        }
        try {
            this.consumer.accept(this.value, this.throwable);
            if (this.throwable == null) {
                this.promise.complete(this.value);
            } else {
                this.promise.completeExceptionally(this.throwable);
            }
        } catch (final PromiseFilterException filter) {
            this.promise.completeExceptionally(filter);
        } catch (final Throwable throwable) {
            Internal.logger().severe(throwable.getMessage(), throwable);
            this.promise.completeExceptionally(throwable);
        }
    }
}
