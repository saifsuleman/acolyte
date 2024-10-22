package net.odinmc.core.common.scheduling;

import java.util.concurrent.CountDownLatch;
import net.odinmc.core.common.terminable.Terminable;
import org.jetbrains.annotations.NotNull;

public interface ServerThreadLock extends Terminable {
    @NotNull
    static ServerThreadLock obtain() {
        return new Impl();
    }

    @Override
    void close();

    final class Impl implements ServerThreadLock {

        private final CountDownLatch done = new CountDownLatch(1);

        private final CountDownLatch obtained = new CountDownLatch(1);

        private Impl() {
            if (ThreadContext.forCurrentThread() == ThreadContext.SYNC) {
                this.obtained.countDown();
                return;
            }
            Schedulers.sync().run(this::signal);
            this.await();
        }

        @Override
        public void close() {
            this.done.countDown();
        }

        private void await() {
            try {
                this.obtained.await();
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void signal() {
            this.obtained.countDown();
            try {
                this.done.await();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
