package net.odinmc.core.common.scheduling.misc;

import java.time.Duration;
import java.util.function.Consumer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.odinmc.core.common.scheduling.Schedulers;
import net.odinmc.core.common.scheduling.Task;
import net.odinmc.core.common.scheduling.ThreadContext;

@Builder
public class Countdown {

    @Builder.Default
    @Getter
    private boolean async = false;

    @Setter
    @Getter
    private int cycles;

    @Setter
    @Builder.Default
    private Consumer<Integer> cycleObserver = i -> {};

    @Setter
    @Builder.Default
    private Runnable onFinish = () -> {};

    @Builder.Default
    private final Duration interval = Duration.ofSeconds(1);

    private transient Task task;

    public static final class CountdownBuilder {

        private CountdownBuilder task(final Task task) {
            throw new IllegalArgumentException("Cannot set task");
        }
    }

    public final void start() {
        this.task = Schedulers.newBuilder().on(this.async ? ThreadContext.ASYNC : ThreadContext.SYNC).every(this.interval).run(this::doTick);
    }

    public boolean isTicking() {
        return this.task != null && !this.task.closed();
    }

    public void doTick() {
        if (this.cycles <= 0) {
            this.onFinish.run();
            this.task.stop();
            return;
        }
        this.cycleObserver.accept(this.cycles);
        this.decrement();
    }

    public final void stop() {
        if (this.isTicking()) {
            this.task.stop();
            this.task = null;
        }
    }

    protected final void decrement() {
        this.cycles--;
    }
}
