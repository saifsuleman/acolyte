package net.odinmc.core.common.scheduling;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

record PromiseOnError<U>(@NotNull Promise<U> promise, @NotNull Consumer<Throwable> consumer, @NotNull Throwable throwable) implements Runnable {
    @Override
    public void run() {
        if (this.promise.cancelled()) {
            return;
        }
        try {
            this.consumer.accept(this.throwable);
            this.promise.completeExceptionally(this.throwable);
        } catch (final PromiseFilterException filter) {
            this.promise.completeExceptionally(filter);
        } catch (final Throwable throwable) {
            Internal.logger().severe(throwable.getMessage(), throwable);
            this.promise.completeExceptionally(throwable);
        }
    }
}
