package net.saifs.odinmc.core.paper.events;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import net.odinmc.core.common.scheduling.Promise;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class AsyncDeferredEvent extends Event implements Executor {

    private final List<Runnable> deferred = new ArrayList<>();
    private Consumer<Promise<Boolean>> hijacker;

    public AsyncDeferredEvent() {
        super(true);
    }

    @Override
    public void execute(@NotNull Runnable runnable) {
        this.deferred.add(runnable);
    }

    public void hijack(Consumer<Promise<Boolean>> hijacker) {
        if (!this.deferred.isEmpty()) {
            throw new IllegalStateException("!this.deferred.isEmpty()");
        }
        if (this.hijacker != null) {
            throw new IllegalStateException("this.hijacker != null");
        }
        this.hijacker = hijacker;
    }

    public Promise<Boolean> executeDeferred(Executor executor) {
        Promise<Boolean> promise = Promise.empty();
        this.stepDeferred(executor, promise);
        return promise;
    }

    private void stepDeferred(Executor executor, Promise<Boolean> promise) {
        if (this.deferred.isEmpty()) {
            if (this.hijacker == null) {
                promise.complete(true);
            } else {
                this.hijacker.accept(promise);
            }
            return;
        }
        var running = new ArrayList<>(this.deferred);
        var runningCount = new AtomicInteger(running.size());
        var success = new AtomicBoolean(true);
        this.deferred.clear();
        for (Runnable runnable : running) {
            executor.execute(() -> {
                try {
                    runnable.run();
                } catch (Exception exception) {
                    success.set(false);
                    exception.printStackTrace();
                }
                if (runningCount.decrementAndGet() > 0) {
                    return;
                }
                if (success.get()) {
                    this.stepDeferred(executor, promise);
                } else {
                    promise.complete(false);
                }
            });
        }
    }
}
