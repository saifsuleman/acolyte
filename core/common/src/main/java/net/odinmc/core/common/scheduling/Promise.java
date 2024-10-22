package net.odinmc.core.common.scheduling;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.odinmc.core.common.delegates.ConsumerToFunction;
import net.odinmc.core.common.delegates.RunnableToFunction;
import net.odinmc.core.common.terminable.Terminable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unchecked", "resource", "unused"})
public interface Promise<V> extends Future<V>, Terminable {
    @NotNull
    static Promise<?> allOf(@NotNull final Promise<?>... promises) {
        return Promise.wrapFuture(CompletableFuture.allOf(Promise.toFutures(promises)));
    }

    @NotNull
    static Collector<Promise<?>, ?, Promise<?>> allOf() {
        return Collectors.collectingAndThen(Collectors.toList(), list -> Promise.allOf(list.toArray(Promise[]::new)));
    }

    @NotNull
    static Promise<Object> anyOf(@NotNull final Promise<?>... promises) {
        return Promise.wrapFuture(CompletableFuture.anyOf(Promise.toFutures(promises)));
    }

    @NotNull
    static Collector<Promise<Object>, ?, Promise<?>> anyOf() {
        return Collectors.collectingAndThen(Collectors.toList(), list -> Promise.anyOf(list.toArray(Promise[]::new)));
    }

    @NotNull
    static <U> Promise<U> completed(final U value) {
        return new PromiseImpl<>(value);
    }

    @NotNull
    static <U> Promise<U> empty() {
        return new PromiseImpl<>();
    }

    @NotNull
    static <U> Promise<U> exceptionally(@NotNull final Throwable exception) {
        return new PromiseImpl<>(exception);
    }

    @NotNull
    static Promise<?> start() {
        return Promise.completed(null);
    }

    @NotNull
    static <U> Promise<U> supplying(@NotNull final ThreadContext context, @NotNull final Supplier<U> supplier) {
        return Promise.<U>empty().supply(context, supplier);
    }

    @NotNull
    static <U> Promise<U> supplyingAsync(@NotNull final Supplier<U> supplier) {
        return Promise.<U>empty().supplyAsync(supplier);
    }

    @NotNull
    static <U> Promise<U> supplyingDelayed(@NotNull final ThreadContext context, @NotNull final Supplier<U> supplier, final long delayTicks) {
        return Promise.supplyingDelayed(context, supplier, Internal.durationFrom(delayTicks));
    }

    @NotNull
    static <U> Promise<U> supplyingDelayed(
            @NotNull final ThreadContext context,
            @NotNull final Supplier<U> supplier,
            final long delay,
            @NotNull final TimeUnit unit
    ) {
        return Promise.supplyingDelayed(context, supplier, Internal.durationFrom(delay, unit));
    }

    @NotNull
    static <U> Promise<U> supplyingDelayed(@NotNull final ThreadContext context, @NotNull final Supplier<U> supplier, @NotNull final Duration delay) {
        return Promise.<U>empty().supplyDelayed(context, supplier, delay);
    }

    @NotNull
    static <U> Promise<U> supplyingDelayedAsync(@NotNull final Supplier<U> supplier, final long delayTicks) {
        return Promise.supplyingDelayedAsync(supplier, Internal.durationFrom(delayTicks));
    }

    @NotNull
    static <U> Promise<U> supplyingDelayedAsync(@NotNull final Supplier<U> supplier, final long delay, @NotNull final TimeUnit unit) {
        return Promise.supplyingDelayedAsync(supplier, Internal.durationFrom(delay, unit));
    }

    @NotNull
    static <U> Promise<U> supplyingDelayedAsync(@NotNull final Supplier<U> supplier, @NotNull final Duration delay) {
        return Promise.<U>empty().supplyDelayedAsync(supplier, delay);
    }

    @NotNull
    static <U> Promise<U> supplyingDelayedSync(@NotNull final Supplier<U> supplier, final long delayTicks) {
        return Promise.supplyingDelayedSync(supplier, Internal.durationFrom(delayTicks));
    }

    @NotNull
    static <U> Promise<U> supplyingDelayedSync(@NotNull final Supplier<U> supplier, final long delay, @NotNull final TimeUnit unit) {
        return Promise.supplyingDelayedSync(supplier, Internal.durationFrom(delay, unit));
    }

    @NotNull
    static <U> Promise<U> supplyingDelayedSync(@NotNull final Supplier<U> supplier, @NotNull final Duration delay) {
        return Promise.<U>empty().supplyDelayedSync(supplier, delay);
    }

    @NotNull
    static <U> Promise<U> supplyingExceptionally(@NotNull final ThreadContext context, @NotNull final Callable<U> callable) {
        return Promise.<U>empty().supplyExceptionally(context, callable);
    }

    @NotNull
    static <U> Promise<U> supplyingExceptionallyAsync(@NotNull final Callable<U> callable) {
        return Promise.<U>empty().supplyExceptionallyAsync(callable);
    }

    @NotNull
    static <U> Promise<U> supplyingExceptionallyDelayed(
            @NotNull final ThreadContext context,
            @NotNull final Callable<U> callable,
            final long delayTicks
    ) {
        return Promise.supplyingExceptionallyDelayed(context, callable, Internal.durationFrom(delayTicks));
    }

    @NotNull
    static <U> Promise<U> supplyingExceptionallyDelayed(
            @NotNull final ThreadContext context,
            @NotNull final Callable<U> callable,
            final long delay,
            @NotNull final TimeUnit unit
    ) {
        return Promise.supplyingExceptionallyDelayed(context, callable, Internal.durationFrom(delay, unit));
    }

    @NotNull
    static <U> Promise<U> supplyingExceptionallyDelayed(
            @NotNull final ThreadContext context,
            @NotNull final Callable<U> callable,
            @NotNull final Duration delay
    ) {
        return Promise.<U>empty().supplyExceptionallyDelayed(context, callable, delay);
    }

    @NotNull
    static <U> Promise<U> supplyingExceptionallyDelayedAsync(@NotNull final Callable<U> callable, final long delayTicks) {
        return Promise.supplyingExceptionallyDelayedAsync(callable, Internal.durationFrom(delayTicks));
    }

    @NotNull
    static <U> Promise<U> supplyingExceptionallyDelayedAsync(@NotNull final Callable<U> callable, final long delay, @NotNull final TimeUnit unit) {
        return Promise.supplyingExceptionallyDelayedAsync(callable, Internal.durationFrom(delay, unit));
    }

    @NotNull
    static <U> Promise<U> supplyingExceptionallyDelayedAsync(@NotNull final Callable<U> callable, @NotNull final Duration delay) {
        return Promise.<U>empty().supplyExceptionallyDelayedAsync(callable, delay);
    }

    @NotNull
    static <U> Promise<U> supplyingExceptionallyDelayedSync(@NotNull final Callable<U> callable, final long delayTicks) {
        return Promise.supplyingExceptionallyDelayedSync(callable, Internal.durationFrom(delayTicks));
    }

    @NotNull
    static <U> Promise<U> supplyingExceptionallyDelayedSync(@NotNull final Callable<U> callable, final long delay, @NotNull final TimeUnit unit) {
        return Promise.supplyingExceptionallyDelayedSync(callable, Internal.durationFrom(delay, unit));
    }

    @NotNull
    static <U> Promise<U> supplyingExceptionallyDelayedSync(@NotNull final Callable<U> callable, @NotNull final Duration delay) {
        return Promise.<U>empty().supplyExceptionallyDelayedSync(callable, delay);
    }

    @NotNull
    static <U> Promise<U> supplyingExceptionallySync(@NotNull final Callable<U> callable) {
        return Promise.<U>empty().supplyExceptionallySync(callable);
    }

    @NotNull
    static <U> Promise<U> supplyingSync(@NotNull final Supplier<U> supplier) {
        return Promise.<U>empty().supplySync(supplier);
    }

    @NotNull
    static <U> Promise<U> wrapFuture(@NotNull final Future<U> future) {
        if (future instanceof CompletionStage<?>) {
            return new PromiseImpl<>(((CompletionStage<U>) future).toCompletableFuture());
        }
        if (future.isDone()) {
            try {
                return Promise.completed(future.get());
            } catch (final ExecutionException e) {
                return Promise.exceptionally(e);
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return Promise.supplyingExceptionallyAsync(future::get);
    }

    @NotNull
    static <U> Promise<U> wrapFutureAsync(@NotNull final Supplier<Future<U>> supplier) {
        final var promise = Promise.<U>empty();
        Schedulers
                .async()
                .run(() -> {
                    final var future = supplier.get();
                    if (future instanceof CompletionStage<?>) {
                        final var completableFuture = ((CompletionStage<U>) future).toCompletableFuture();
                        try {
                            promise.complete(completableFuture.join());
                        } catch (final Throwable e) {
                            promise.completeExceptionally(e);
                        }
                    } else if (future.isDone()) {
                        try {
                            promise.complete(future.get());
                        } catch (final Throwable e) {
                            promise.completeExceptionally(e);
                        }
                    } else {
                        promise.supplyExceptionallyAsync(future::get);
                    }
                });
        return promise;
    }

    @NotNull
    private static CompletableFuture<?>[] toFutures(@NotNull final Promise<?>[] promises) {
        final CompletableFuture<?>[] futures = new CompletableFuture[promises.length];
        for (int i = 0; i < promises.length; i++) {
            futures[i] = promises[i].future();
        }
        return futures;
    }

    default boolean cancel() {
        return this.cancel(true);
    }

    @ApiStatus.Internal
    boolean cancelled();

    @Override
    default void close() {
        this.cancel();
    }

    default boolean closed() {
        return this.isCancelled();
    }

    @ApiStatus.Internal
    default void complete(final V value) {
        if (!this.cancelled()) {
            this.future().complete(value);
        }
    }

    @ApiStatus.Internal
    default void completeExceptionally(@NotNull final Throwable ex) {
        if (!this.cancelled()) {
            this.future().completeExceptionally(ex);
        }
    }

    @NotNull
    default Promise<V> exceptionally(@NotNull final ThreadContext context, @NotNull final Function<Throwable, ? extends V> function) {
        return switch (context) {
            case SYNC -> this.exceptionallySync(function);
            case ASYNC -> this.exceptionallyAsync(function);
        };
    }

    @NotNull
    default Promise<V> exceptionallyAsync(@NotNull final Function<Throwable, ? extends V> function) {
        return this.whenError((p, t) -> Executors.async(new PromiseApply<>(p, function, t)));
    }

    @NotNull
    default Promise<V> exceptionallyDelayed(
            @NotNull final ThreadContext context,
            @NotNull final Function<Throwable, ? extends V> function,
            final long delayTicks
    ) {
        return this.exceptionallyDelayed(context, function, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<V> exceptionallyDelayed(
            @NotNull final ThreadContext context,
            @NotNull final Function<Throwable, ? extends V> function,
            final long delay,
            @NotNull final TimeUnit unit
    ) {
        return this.exceptionallyDelayed(context, function, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default Promise<V> exceptionallyDelayed(
            @NotNull final ThreadContext context,
            @NotNull final Function<Throwable, ? extends V> function,
            @NotNull final Duration delay
    ) {
        return switch (context) {
            case SYNC -> this.exceptionallyDelayedSync(function, delay);
            case ASYNC -> this.exceptionallyDelayedAsync(function, delay);
        };
    }

    @NotNull
    default Promise<V> exceptionallyDelayedAsync(@NotNull final Function<Throwable, ? extends V> function, final long delayTicks) {
        return this.exceptionallyDelayedAsync(function, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<V> exceptionallyDelayedAsync(
            @NotNull final Function<Throwable, ? extends V> function,
            final long delay,
            @NotNull final TimeUnit unit
    ) {
        return this.exceptionallyDelayedAsync(function, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default Promise<V> exceptionallyDelayedAsync(@NotNull final Function<Throwable, ? extends V> function, @NotNull final Duration delay) {
        return this.whenError((p, t) -> Executors.asyncDelayed(new PromiseApply<>(p, function, t), delay));
    }

    @NotNull
    default Promise<V> exceptionallyDelayedSync(@NotNull final Function<Throwable, ? extends V> function, final long delayTicks) {
        return this.exceptionallyDelayedSync(function, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<V> exceptionallyDelayedSync(
            @NotNull final Function<Throwable, ? extends V> function,
            final long delay,
            @NotNull final TimeUnit unit
    ) {
        return this.exceptionallyDelayedSync(function, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default Promise<V> exceptionallyDelayedSync(@NotNull final Function<Throwable, ? extends V> function, @NotNull final Duration delay) {
        return this.whenError((p, t) -> Executors.syncDelayed(new PromiseApply<>(p, function, t), delay));
    }

    @NotNull
    default Promise<V> exceptionallySync(@NotNull final Function<Throwable, ? extends V> function) {
        return this.whenError((p, t) -> Executors.sync(new PromiseApply<>(p, function, t)));
    }

    @NotNull
    CompletableFuture<V> future();

    @Nullable
    @Contract("!null -> !null")
    default V getNow(final V valueIfAbsent) {
        return this.future().getNow(valueIfAbsent);
    }

    @NotNull
    default <U> Promise<U> handle(@NotNull final ThreadContext context, @NotNull final BiFunction<V, Throwable, U> function) {
        return switch (context) {
            case SYNC -> this.handleSync(function);
            case ASYNC -> this.handleAsync(function);
        };
    }

    @NotNull
    default <U> Promise<U> handleAsync(@NotNull final BiFunction<V, Throwable, U> function) {
        return this.whenComplete(
                (p, v) -> Executors.async(new PromiseHandle<>(p, function, v, null)),
                (p, t) -> Executors.async(new PromiseHandle<>(p, function, null, t))
        );
    }

    @NotNull
    default <U> Promise<U> handleDelayed(
            @NotNull final ThreadContext context,
            @NotNull final BiFunction<V, Throwable, U> function,
            final long delayTicks
    ) {
        return this.handleDelayed(context, function, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default <U> Promise<U> handleDelayed(
            @NotNull final ThreadContext context,
            @NotNull final BiFunction<V, Throwable, U> function,
            final long delay,
            @NotNull final TimeUnit unit
    ) {
        return this.handleDelayed(context, function, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default <U> Promise<U> handleDelayed(
            @NotNull final ThreadContext context,
            @NotNull final BiFunction<V, Throwable, U> function,
            @NotNull final Duration delay
    ) {
        return switch (context) {
            case SYNC -> this.handleDelayedSync(function, delay);
            case ASYNC -> this.handleDelayedAsync(function, delay);
        };
    }

    @NotNull
    default <U> Promise<U> handleDelayedAsync(@NotNull final BiFunction<V, Throwable, U> function, final long delayTicks) {
        return this.handleDelayedAsync(function, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default <U> Promise<U> handleDelayedAsync(@NotNull final BiFunction<V, Throwable, U> function, final long delay, @NotNull final TimeUnit unit) {
        return this.handleDelayedAsync(function, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default <U> Promise<U> handleDelayedAsync(@NotNull final BiFunction<V, Throwable, U> function, @NotNull final Duration delay) {
        return this.whenComplete(
                (p, v) -> Executors.asyncDelayed(new PromiseHandle<>(p, function, v, null), delay),
                (p, t) -> Executors.asyncDelayed(new PromiseHandle<>(p, function, null, t), delay)
        );
    }

    @NotNull
    default <U> Promise<U> handleDelayedSync(@NotNull final BiFunction<V, Throwable, U> function, final long delayTicks) {
        return this.handleDelayedSync(function, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default <U> Promise<U> handleDelayedSync(@NotNull final BiFunction<V, Throwable, U> function, final long delay, @NotNull final TimeUnit unit) {
        return this.handleDelayedSync(function, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default <U> Promise<U> handleDelayedSync(@NotNull final BiFunction<V, Throwable, U> function, @NotNull final Duration delay) {
        return this.whenComplete(
                (p, v) -> Executors.syncDelayed(new PromiseHandle<>(p, function, v, null), delay),
                (p, t) -> Executors.syncDelayed(new PromiseHandle<>(p, function, null, t), delay)
        );
    }

    @NotNull
    default <U> Promise<U> handleSync(@NotNull final BiFunction<V, Throwable, U> function) {
        return this.whenComplete(
                (p, v) -> Executors.sync(new PromiseHandle<>(p, function, v, null)),
                (p, t) -> Executors.sync(new PromiseHandle<>(p, function, null, t))
        );
    }

    @Override
    default boolean isCancelled() {
        return this.future().isCancelled();
    }

    @Override
    default boolean isDone() {
        return this.future().isDone();
    }

    @Override
    default V get() throws InterruptedException, ExecutionException {
        return this.future().get();
    }

    @Override
    default V get(final long timeout, @NotNull final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.future().get(timeout, unit);
    }

    @Nullable
    default V join() {
        return this.future().join();
    }

    @NotNull
    default Promise<V> onError(@NotNull final ThreadContext context, @NotNull final Consumer<Throwable> consumer) {
        return switch (context) {
            case SYNC -> this.onErrorSync(consumer);
            case ASYNC -> this.onErrorAsync(consumer);
        };
    }

    @NotNull
    default Promise<V> onErrorAsync(@NotNull final Consumer<Throwable> consumer) {
        return this.whenError((p, t) -> Executors.async(new PromiseOnError<>(p, consumer, t)));
    }

    @NotNull
    default Promise<V> onErrorDelayed(@NotNull final ThreadContext context, @NotNull final Consumer<Throwable> consumer, final long delayTicks) {
        return this.onErrorDelayed(context, consumer, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<V> onErrorDelayed(
            @NotNull final ThreadContext context,
            @NotNull final Consumer<Throwable> consumer,
            final long delay,
            @NotNull final TimeUnit unit
    ) {
        return this.onErrorDelayed(context, consumer, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default Promise<V> onErrorDelayed(
            @NotNull final ThreadContext context,
            @NotNull final Consumer<Throwable> consumer,
            @NotNull final Duration delay
    ) {
        return switch (context) {
            case SYNC -> this.onErrorDelayedSync(consumer, delay);
            case ASYNC -> this.onErrorDelayedAsync(consumer, delay);
        };
    }

    @NotNull
    default Promise<V> onErrorDelayedAsync(@NotNull final Consumer<Throwable> consumer, final long delayTicks) {
        return this.onErrorDelayedAsync(consumer, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<V> onErrorDelayedAsync(@NotNull final Consumer<Throwable> consumer, final long delay, @NotNull final TimeUnit unit) {
        return this.onErrorDelayedAsync(consumer, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default Promise<V> onErrorDelayedAsync(@NotNull final Consumer<Throwable> consumer, @NotNull final Duration delay) {
        return this.whenError((p, t) -> Executors.asyncDelayed(new PromiseOnError<>(p, consumer, t), delay));
    }

    @NotNull
    default Promise<V> onErrorDelayedSync(@NotNull final Consumer<Throwable> consumer, final long delayTicks) {
        return this.onErrorDelayedSync(consumer, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<V> onErrorDelayedSync(@NotNull final Consumer<Throwable> consumer, final long delay, @NotNull final TimeUnit unit) {
        return this.onErrorDelayedSync(consumer, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default Promise<V> onErrorDelayedSync(@NotNull final Consumer<Throwable> consumer, @NotNull final Duration delay) {
        return this.whenError((p, t) -> Executors.syncDelayed(new PromiseOnError<>(p, consumer, t), delay));
    }

    @NotNull
    default Promise<V> onErrorSync(@NotNull final Consumer<Throwable> consumer) {
        return this.whenError((p, t) -> Executors.sync(new PromiseOnError<>(p, consumer, t)));
    }

    @NotNull
    default Promise<V> printException() {
        return this.whenError((p, t) -> t.printStackTrace());
    }

    @NotNull
    Promise<V> setChild(@Nullable Promise<?> child);

    @NotNull
    Promise<V> setParent(@Nullable Promise<?> parent);

    @NotNull
    Promise<V> supply(V value);

    @NotNull
    default Promise<V> supply(@NotNull final ThreadContext context, @NotNull final Supplier<V> supplier) {
        return switch (context) {
            case SYNC -> this.supplySync(supplier);
            case ASYNC -> this.supplyAsync(supplier);
        };
    }

    @NotNull
    Promise<V> supplyAsync(@NotNull Supplier<V> supplier);

    @NotNull
    default Promise<V> supplyDelayed(@NotNull final ThreadContext context, @NotNull final Supplier<V> supplier, final long delayTicks) {
        return this.supplyDelayed(context, supplier, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<V> supplyDelayed(
            @NotNull final ThreadContext context,
            @NotNull final Supplier<V> supplier,
            final long delay,
            @NotNull final TimeUnit unit
    ) {
        return this.supplyDelayed(context, supplier, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default Promise<V> supplyDelayed(@NotNull final ThreadContext context, @NotNull final Supplier<V> supplier, @NotNull final Duration delay) {
        return switch (context) {
            case SYNC -> this.supplyDelayedSync(supplier, delay);
            case ASYNC -> this.supplyDelayedAsync(supplier, delay);
        };
    }

    @NotNull
    default Promise<V> supplyDelayedAsync(@NotNull final Supplier<V> supplier, final long delayTicks) {
        return this.supplyDelayedAsync(supplier, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<V> supplyDelayedAsync(@NotNull final Supplier<V> supplier, final long delay, @NotNull final TimeUnit unit) {
        return this.supplyDelayedAsync(supplier, Duration.of(delay, unit.toChronoUnit()));
    }

    @NotNull
    Promise<V> supplyDelayedAsync(@NotNull Supplier<V> supplier, @NotNull Duration delay);

    @NotNull
    default Promise<V> supplyDelayedSync(@NotNull final Supplier<V> supplier, final long delayTicks) {
        return this.supplyDelayedSync(supplier, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<V> supplyDelayedSync(@NotNull final Supplier<V> supplier, final long delay, @NotNull final TimeUnit unit) {
        return this.supplyDelayedSync(supplier, Internal.durationFrom(delay, unit));
    }

    @NotNull
    Promise<V> supplyDelayedSync(@NotNull Supplier<V> supplier, @NotNull Duration delay);

    @NotNull
    Promise<V> supplyException(@NotNull Throwable exception);

    @NotNull
    default Promise<V> supplyExceptionally(@NotNull final ThreadContext context, @NotNull final Callable<V> callable) {
        return switch (context) {
            case SYNC -> this.supplyExceptionallySync(callable);
            case ASYNC -> this.supplyExceptionallyAsync(callable);
        };
    }

    @NotNull
    Promise<V> supplyExceptionallyAsync(@NotNull Callable<V> callable);

    @NotNull
    default Promise<V> supplyExceptionallyDelayed(@NotNull final ThreadContext context, @NotNull final Callable<V> callable, final long delayTicks) {
        return this.supplyExceptionallyDelayed(context, callable, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<V> supplyExceptionallyDelayed(
            @NotNull final ThreadContext context,
            @NotNull final Callable<V> callable,
            final long delay,
            @NotNull final TimeUnit unit
    ) {
        return this.supplyExceptionallyDelayed(context, callable, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default Promise<V> supplyExceptionallyDelayed(
            @NotNull final ThreadContext context,
            @NotNull final Callable<V> callable,
            @NotNull final Duration delay
    ) {
        return switch (context) {
            case SYNC -> this.supplyExceptionallyDelayedSync(callable, delay);
            case ASYNC -> this.supplyExceptionallyDelayedAsync(callable, delay);
        };
    }

    @NotNull
    default Promise<V> supplyExceptionallyDelayedAsync(@NotNull final Callable<V> callable, final long delayTicks) {
        return this.supplyExceptionallyDelayedAsync(callable, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<V> supplyExceptionallyDelayedAsync(@NotNull final Callable<V> callable, final long delay, @NotNull final TimeUnit unit) {
        return this.supplyExceptionallyDelayedAsync(callable, Internal.durationFrom(delay, unit));
    }

    @NotNull
    Promise<V> supplyExceptionallyDelayedAsync(@NotNull final Callable<V> callable, @NotNull final Duration delay);

    @NotNull
    default Promise<V> supplyExceptionallyDelayedSync(@NotNull final Callable<V> callable, final long delayTicks) {
        return this.supplyExceptionallyDelayedSync(callable, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<V> supplyExceptionallyDelayedSync(@NotNull final Callable<V> callable, final long delay, @NotNull final TimeUnit unit) {
        return this.supplyExceptionallyDelayedSync(callable, Internal.durationFrom(delay, unit));
    }

    @NotNull
    Promise<V> supplyExceptionallyDelayedSync(@NotNull final Callable<V> callable, @NotNull final Duration delay);

    @NotNull
    Promise<V> supplyExceptionallySync(@NotNull Callable<V> callable);

    @NotNull
    Promise<V> supplySync(@NotNull Supplier<V> supplier);

    @NotNull
    default Promise<?> thenAccept(@NotNull final ThreadContext context, @NotNull final Consumer<V> action) {
        return switch (context) {
            case SYNC -> this.thenAcceptSync(action);
            case ASYNC -> this.thenAcceptAsync(action);
        };
    }

    @NotNull
    default Promise<?> thenAcceptAsync(@NotNull final Consumer<V> action) {
        return this.thenApplyAsync(v -> {
            action.accept(v);
            return null;
        });
    }

    @NotNull
    default Promise<?> thenAcceptDelayed(@NotNull final ThreadContext context, @NotNull final Consumer<V> action, final long delayTicks) {
        return this.thenAcceptDelayed(context, action, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<?> thenAcceptDelayed(
            @NotNull final ThreadContext context,
            @NotNull final Consumer<V> action,
            final long delay,
            @NotNull final TimeUnit unit
    ) {
        return this.thenAcceptDelayed(context, action, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default Promise<?> thenAcceptDelayed(@NotNull final ThreadContext context, @NotNull final Consumer<V> action, @NotNull final Duration delay) {
        return switch (context) {
            case SYNC -> this.thenAcceptDelayedSync(action, delay);
            case ASYNC -> this.thenAcceptDelayedAsync(action, delay);
        };
    }

    @NotNull
    default Promise<?> thenAcceptDelayedAsync(@NotNull final Consumer<V> action, final long delayTicks) {
        return this.thenAcceptDelayedAsync(action, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<?> thenAcceptDelayedAsync(@NotNull final Consumer<V> action, final long delay, @NotNull final TimeUnit unit) {
        return this.thenAcceptDelayedAsync(action, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default Promise<?> thenAcceptDelayedAsync(@NotNull final Consumer<V> action, @NotNull final Duration delay) {
        return this.thenApplyDelayedAsync(new ConsumerToFunction<>(action), delay);
    }

    @NotNull
    default Promise<?> thenAcceptDelayedSync(@NotNull final Consumer<V> action, final long delayTicks) {
        return this.thenAcceptDelayedSync(action, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<?> thenAcceptDelayedSync(@NotNull final Consumer<V> action, final long delay, @NotNull final TimeUnit unit) {
        return this.thenAcceptDelayedSync(action, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default Promise<?> thenAcceptDelayedSync(@NotNull final Consumer<V> action, @NotNull final Duration delay) {
        return this.thenApplyDelayedSync(new ConsumerToFunction<>(action), delay);
    }

    @NotNull
    default Promise<?> thenAcceptSync(@NotNull final Consumer<V> action) {
        return this.thenApplySync(new ConsumerToFunction<>(action));
    }

    @NotNull
    default <U> Promise<U> thenApply(@NotNull final ThreadContext context, @NotNull final Function<V, ? extends U> function) {
        return switch (context) {
            case SYNC -> this.thenApplySync(function);
            case ASYNC -> this.thenApplyAsync(function);
        };
    }

    @NotNull
    default <U> Promise<U> thenApplyAsync(@NotNull final Function<V, ? extends U> function) {
        return this.whenSuccess((p, v) -> Executors.async(new PromiseApply<>(p, function, v)));
    }

    @NotNull
    default <U> Promise<U> thenApplyDelayed(
            @NotNull final ThreadContext context,
            @NotNull final Function<V, ? extends U> function,
            final long delayTicks
    ) {
        return this.thenApplyDelayed(context, function, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default <U> Promise<U> thenApplyDelayed(
            @NotNull final ThreadContext context,
            @NotNull final Function<V, ? extends U> function,
            final long delay,
            @NotNull final TimeUnit unit
    ) {
        return this.thenApplyDelayed(context, function, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default <U> Promise<U> thenApplyDelayed(
            @NotNull final ThreadContext context,
            @NotNull final Function<V, ? extends U> function,
            @NotNull final Duration delay
    ) {
        return switch (context) {
            case SYNC -> this.thenApplyDelayedSync(function, delay);
            case ASYNC -> this.thenApplyDelayedAsync(function, delay);
        };
    }

    @NotNull
    default <U> Promise<U> thenApplyDelayedAsync(@NotNull final Function<V, ? extends U> function, final long delayTicks) {
        return this.thenApplyDelayedAsync(function, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default <U> Promise<U> thenApplyDelayedAsync(@NotNull final Function<V, ? extends U> function, final long delay, @NotNull final TimeUnit unit) {
        return this.thenApplyDelayedAsync(function, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default <U> Promise<U> thenApplyDelayedAsync(@NotNull final Function<V, ? extends U> function, @NotNull final Duration delay) {
        return this.whenSuccess((p, v) -> Executors.asyncDelayed(new PromiseApply<>(p, function, v), delay));
    }

    @NotNull
    default <U> Promise<U> thenApplyDelayedSync(@NotNull final Function<V, ? extends U> function, final long delayTicks) {
        return this.thenApplyDelayedSync(function, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default <U> Promise<U> thenApplyDelayedSync(@NotNull final Function<V, ? extends U> function, final long delay, @NotNull final TimeUnit unit) {
        return this.thenApplyDelayedSync(function, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default <U> Promise<U> thenApplyDelayedSync(@NotNull final Function<V, ? extends U> function, @NotNull final Duration delay) {
        return this.whenSuccess((p, v) -> Executors.syncDelayed(new PromiseApply<>(p, function, v), delay));
    }

    @NotNull
    default <U> Promise<U> thenApplySync(@NotNull final Function<V, ? extends U> function) {
        return this.whenSuccess((p, v) -> Executors.sync(new PromiseApply<>(p, function, v)));
    }

    @NotNull
    default <U> Promise<U> thenCompose(@NotNull final ThreadContext context, @NotNull final Function<V, ? extends Promise<U>> function) {
        return switch (context) {
            case SYNC -> this.thenComposeSync(function);
            case ASYNC -> this.thenComposeAsync(function);
        };
    }

    @NotNull
    default <U> Promise<U> thenComposeAsync(@NotNull final Function<V, ? extends Promise<U>> function) {
        return this.whenSuccess((p, v) -> Executors.async(new PromiseCompose<>(p, function, v, false)));
    }

    @NotNull
    default <U> Promise<U> thenComposeDelayedAsync(@NotNull final Function<V, ? extends Promise<U>> function, final long delayTicks) {
        return this.thenComposeDelayedAsync(function, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default <U> Promise<U> thenComposeDelayedAsync(
            @NotNull final Function<V, ? extends Promise<U>> function,
            final long delay,
            @NotNull final TimeUnit unit
    ) {
        return this.thenComposeDelayedAsync(function, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default <U> Promise<U> thenComposeDelayedAsync(@NotNull final Function<V, ? extends Promise<U>> function, @NotNull final Duration delay) {
        return this.whenSuccess((p, v) -> Executors.asyncDelayed(new PromiseCompose<>(p, function, v, false), delay));
    }

    @NotNull
    default <U> Promise<U> thenComposeDelayedSync(
            @NotNull final ThreadContext context,
            @NotNull final Function<V, ? extends Promise<U>> function,
            final long delayTicks
    ) {
        return this.thenComposeDelayedSync(context, function, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default <U> Promise<U> thenComposeDelayedSync(
            @NotNull final ThreadContext context,
            @NotNull final Function<V, ? extends Promise<U>> function,
            final long delay,
            @NotNull final TimeUnit unit
    ) {
        return this.thenComposeDelayedSync(context, function, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default <U> Promise<U> thenComposeDelayedSync(
            @NotNull final ThreadContext context,
            @NotNull final Function<V, ? extends Promise<U>> function,
            @NotNull final Duration delay
    ) {
        return switch (context) {
            case SYNC -> this.thenComposeDelayedSync(function, delay);
            case ASYNC -> this.thenComposeDelayedAsync(function, delay);
        };
    }

    @NotNull
    default <U> Promise<U> thenComposeDelayedSync(@NotNull final Function<V, ? extends Promise<U>> function, final long delayTicks) {
        return this.thenComposeDelayedSync(function, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default <U> Promise<U> thenComposeDelayedSync(
            @NotNull final Function<V, ? extends Promise<U>> function,
            final long delay,
            @NotNull final TimeUnit unit
    ) {
        return this.thenComposeDelayedSync(function, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default <U> Promise<U> thenComposeDelayedSync(@NotNull final Function<V, ? extends Promise<U>> function, @NotNull final Duration delay) {
        return this.whenSuccess((p, v) -> Executors.syncDelayed(new PromiseCompose<>(p, function, v, true), delay));
    }

    @NotNull
    default <U> Promise<U> thenComposeSync(@NotNull final Function<V, ? extends Promise<U>> function) {
        return this.whenSuccess((p, v) -> Executors.sync(new PromiseCompose<>(p, function, v, true)));
    }

    @NotNull
    default Promise<V> thenFilter(@NotNull final ThreadContext context, @NotNull final Predicate<V> filter) {
        return switch (context) {
            case SYNC -> this.thenFilterSync(filter);
            case ASYNC -> this.thenFilterAsync(filter);
        };
    }

    @NotNull
    default Promise<V> thenFilterAsync(@NotNull final Predicate<V> filter) {
        return this.whenSuccess((p, v) -> Executors.async(new PromiseFilter<>(p, filter, v)));
    }

    @NotNull
    default Promise<V> thenFilterDelayed(@NotNull final ThreadContext context, @NotNull final Predicate<V> filter, final long delayTicks) {
        return this.thenFilterDelayed(context, filter, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<V> thenFilterDelayed(
            @NotNull final ThreadContext context,
            @NotNull final Predicate<V> filter,
            final long delay,
            @NotNull final TimeUnit unit
    ) {
        return this.thenFilterDelayed(context, filter, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default Promise<V> thenFilterDelayed(@NotNull final ThreadContext context, @NotNull final Predicate<V> filter, @NotNull final Duration delay) {
        return switch (context) {
            case SYNC -> this.thenFilterDelayedSync(filter, delay);
            case ASYNC -> this.thenFilterDelayedAsync(filter, delay);
        };
    }

    @NotNull
    default Promise<V> thenFilterDelayedAsync(@NotNull final Predicate<V> filter, final long delayTicks) {
        return this.thenFilterDelayedAsync(filter, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<V> thenFilterDelayedAsync(@NotNull final Predicate<V> filter, final long delay, @NotNull final TimeUnit unit) {
        return this.thenFilterDelayedAsync(filter, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default Promise<V> thenFilterDelayedAsync(@NotNull final Predicate<V> filter, @NotNull final Duration delay) {
        return this.whenSuccess((p, v) -> Executors.asyncDelayed(new PromiseFilter<>(p, filter, v), delay));
    }

    @NotNull
    default Promise<V> thenFilterDelayedSync(@NotNull final Predicate<V> filter, final long delayTicks) {
        return this.thenFilterDelayedSync(filter, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<V> thenFilterDelayedSync(@NotNull final Predicate<V> filter, final long delay, @NotNull final TimeUnit unit) {
        return this.thenFilterDelayedSync(filter, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default Promise<V> thenFilterDelayedSync(@NotNull final Predicate<V> filter, @NotNull final Duration delay) {
        return this.whenSuccess((p, v) -> Executors.syncDelayed(new PromiseFilter<>(p, filter, v), delay));
    }

    @NotNull
    default Promise<V> thenFilterSync(@NotNull final Predicate<V> filter) {
        return this.whenSuccess((p, v) -> Executors.sync(new PromiseFilter<>(p, filter, v)));
    }

    @NotNull
    default Promise<?> thenRun(@NotNull final ThreadContext context, @NotNull final Runnable action) {
        return switch (context) {
            case SYNC -> this.thenRunSync(action);
            case ASYNC -> this.thenRunAsync(action);
        };
    }

    @NotNull
    default Promise<?> thenRunAsync(@NotNull final Runnable action) {
        return this.thenApplyAsync(new RunnableToFunction<>(action));
    }

    @NotNull
    default Promise<?> thenRunDelayed(@NotNull final ThreadContext context, @NotNull final Runnable action, final long delayTicks) {
        return this.thenRunDelayed(context, action, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<?> thenRunDelayed(
            @NotNull final ThreadContext context,
            @NotNull final Runnable action,
            final long delay,
            @NotNull final TimeUnit unit
    ) {
        return this.thenRunDelayed(context, action, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default Promise<?> thenRunDelayed(@NotNull final ThreadContext context, @NotNull final Runnable action, @NotNull final Duration delay) {
        return switch (context) {
            case SYNC -> this.thenRunDelayedSync(action, delay);
            case ASYNC -> this.thenRunDelayedAsync(action, delay);
        };
    }

    @NotNull
    default Promise<?> thenRunDelayedAsync(@NotNull final Runnable action, final long delayTicks) {
        return this.thenRunDelayedAsync(action, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<?> thenRunDelayedAsync(@NotNull final Runnable action, final long delay, @NotNull final TimeUnit unit) {
        return this.thenRunDelayedAsync(action, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default Promise<?> thenRunDelayedAsync(@NotNull final Runnable action, @NotNull final Duration delay) {
        return this.thenApplyDelayedAsync(new RunnableToFunction<>(action), delay);
    }

    @NotNull
    default Promise<?> thenRunDelayedSync(@NotNull final Runnable action, final long delayTicks) {
        return this.thenRunDelayedSync(action, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<?> thenRunDelayedSync(@NotNull final Runnable action, final long delay, @NotNull final TimeUnit unit) {
        return this.thenRunDelayedSync(action, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default Promise<?> thenRunDelayedSync(@NotNull final Runnable action, @NotNull final Duration delay) {
        return this.thenApplyDelayedSync(new RunnableToFunction<>(action), delay);
    }

    @NotNull
    default Promise<?> thenRunSync(@NotNull final Runnable action) {
        return this.thenApplySync(new RunnableToFunction<>(action));
    }

    @NotNull
    default Promise<V> timeout(final long timeoutTicks) {
        return this.timeout(Internal.durationFrom(timeoutTicks));
    }

    @NotNull
    default Promise<V> timeout(@NotNull final Duration timeout) {
        return this.timeout(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    @NotNull
    default Promise<V> timeout(final long timeout, @NotNull final TimeUnit unit) {
        this.future().orTimeout(timeout, unit);
        return this;
    }

    @NotNull
    default Promise<V> whenComplete(@NotNull final ThreadContext context, @NotNull final BiConsumer<V, Throwable> action) {
        return switch (context) {
            case SYNC -> this.whenCompleteSync(action);
            case ASYNC -> this.whenCompleteAsync(action);
        };
    }

    @NotNull
    default Promise<V> whenCompleteAsync(@NotNull final BiConsumer<V, Throwable> action) {
        return this.whenComplete(
                (p, v) -> Executors.async(new PromiseWhenComplete<>(p, action, v, null)),
                (p, t) -> Executors.async(new PromiseOnError<>(p, e -> action.accept(null, e), t))
        );
    }

    @NotNull
    default Promise<V> whenCompleteDelayed(
            @NotNull final ThreadContext context,
            @NotNull final BiConsumer<V, Throwable> action,
            final long delayTicks
    ) {
        return this.whenCompleteDelayed(context, action, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<V> whenCompleteDelayed(
            @NotNull final ThreadContext context,
            @NotNull final BiConsumer<V, Throwable> action,
            final long delay,
            @NotNull final TimeUnit unit
    ) {
        return this.whenCompleteDelayed(context, action, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default Promise<V> whenCompleteDelayed(
            @NotNull final ThreadContext context,
            @NotNull final BiConsumer<V, Throwable> action,
            @NotNull final Duration delay
    ) {
        return switch (context) {
            case SYNC -> this.whenCompleteDelayedSync(action, delay);
            case ASYNC -> this.whenCompleteDelayedAsync(action, delay);
        };
    }

    @NotNull
    default Promise<V> whenCompleteDelayedAsync(@NotNull final BiConsumer<V, Throwable> action, final long delayTicks) {
        return this.whenCompleteDelayedAsync(action, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<V> whenCompleteDelayedAsync(@NotNull final BiConsumer<V, Throwable> action, final long delay, @NotNull final TimeUnit unit) {
        return this.whenCompleteDelayedAsync(action, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default Promise<V> whenCompleteDelayedAsync(@NotNull final BiConsumer<V, Throwable> action, @NotNull final Duration delay) {
        return this.whenComplete(
                (p, v) -> Executors.asyncDelayed(new PromiseWhenComplete<>(p, action, v, null), delay),
                (p, t) -> Executors.asyncDelayed(new PromiseOnError<>(p, e -> action.accept(null, e), t), delay)
        );
    }

    @NotNull
    default Promise<V> whenCompleteDelayedSync(@NotNull final BiConsumer<V, Throwable> action, final long delayTicks) {
        return this.whenCompleteDelayedSync(action, Internal.durationFrom(delayTicks));
    }

    @NotNull
    default Promise<V> whenCompleteDelayedSync(@NotNull final BiConsumer<V, Throwable> action, final long delay, @NotNull final TimeUnit unit) {
        return this.whenCompleteDelayedSync(action, Internal.durationFrom(delay, unit));
    }

    @NotNull
    default Promise<V> whenCompleteDelayedSync(@NotNull final BiConsumer<V, Throwable> action, @NotNull final Duration delay) {
        return this.whenComplete(
                (p, v) -> Executors.syncDelayed(new PromiseWhenComplete<>(p, action, v, null), delay),
                (p, t) -> Executors.syncDelayed(new PromiseOnError<>(p, e -> action.accept(null, e), t), delay)
        );
    }

    @NotNull
    default Promise<V> whenCompleteSync(@NotNull final BiConsumer<V, Throwable> action) {
        return this.whenComplete(
                (p, v) -> Executors.sync(new PromiseWhenComplete<>(p, action, v, null)),
                (p, t) -> Executors.sync(new PromiseOnError<>(p, e -> action.accept(null, e), t))
        );
    }

    @NotNull
    private <U> Promise<U> whenComplete(
            @NotNull final BiConsumer<Promise<U>, V> onSuccess,
            @NotNull final BiConsumer<Promise<U>, Throwable> onError
    ) {
        final var promise = Promise.<U>empty().setParent(this);
        this.future()
                .whenComplete((v, t) -> {
                    if (t == null) {
                        onSuccess.accept(promise, v);
                    } else {
                        onError.accept(promise, t);
                    }
                });
        return promise;
    }

    @NotNull
    private Promise<V> whenError(@NotNull final BiConsumer<Promise<V>, Throwable> onError) {
        return this.whenComplete(Promise::complete, onError);
    }

    @NotNull
    private <U> Promise<U> whenSuccess(@NotNull final BiConsumer<Promise<U>, V> onSuccess) {
        return this.whenComplete(onSuccess, Promise::completeExceptionally);
    }
}
