package net.saifs.odinmc.events;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.saifs.odinmc.definitions.Self;

public interface SubscriptionBuilder<
    Event,
    Sb extends Subscription,
    HandlerList extends FunctionalHandlerList<Event, Sb, HandlerList>,
    Slf extends SubscriptionBuilder<Event, Sb, HandlerList, Slf>
>
    extends Self<Slf> {
    default HandlerList biConsumer(final BiConsumer<Sb, Event> handler) {
        return this.handlers().biConsumer(handler);
    }

    default Sb biHandler(final BiConsumer<Sb, Event> handler) {
        return this.biConsumer(handler).register();
    }

    default HandlerList consumer(final Consumer<Event> handler) {
        return this.handlers().consumer(handler);
    }

    default Slf expireAfter(final Duration duration) {
        final var dur = duration.toNanos();
        if (dur < 1) {
            throw new IllegalArgumentException("duration is less than 1ms!");
        }
        final var expiry = Math.addExact(System.nanoTime(), dur);
        return this.expireIf((__, ___) -> System.nanoTime() > expiry, ExpiryTestStage.PRE);
    }

    default Slf expireAfter(final long duration, final TimeUnit unit) {
        return this.expireAfter(Duration.of(duration, unit.toChronoUnit()));
    }

    default Slf expireAfter(final long maxCalls) {
        if (maxCalls < 1) {
            throw new IllegalArgumentException("maxCalls is less than 1!");
        }
        return this.expireIf((handler, event) -> handler.callCounter() >= maxCalls, ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
    }

    default Slf expireIf(final Predicate<Event> predicate) {
        return this.expireIf((__, e) -> predicate.test(e), ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
    }

    Slf expireIf(BiPredicate<Sb, Event> predicate, ExpiryTestStage... testPoints);

    Slf filter(Predicate<Event> predicate);

    default Slf filterNot(final Predicate<Event> predicate) {
        return this.filter(Predicate.not(predicate));
    }

    default Sb handler(final Consumer<Event> handler) {
        return this.biHandler((__, e) -> handler.accept(e));
    }

    default Sb handler(final Runnable handler) {
        return this.handler(__ -> handler.run());
    }

    HandlerList handlers();

    interface Get<Event, Sb extends Subscription> {
        Predicate<Event> filter();
        BiPredicate<Sb, Event> midExpiryTest();
        BiPredicate<Sb, Event> postExpiryTest();
        BiPredicate<Sb, Event> preExpiryTest();
    }

    abstract class Base<
        Event,
        Sb extends Subscription,
        HandlerList extends FunctionalHandlerList<Event, Sb, HandlerList>,
        Slf extends SubscriptionBuilder<Event, Sb, HandlerList, Slf>
    >
        implements SubscriptionBuilder<Event, Sb, HandlerList, Slf>, Get<Event, Sb> {

        private Predicate<Event> filter = __ -> true;
        private BiPredicate<Sb, Event> midExpiryTest = (__, ___) -> false;
        private BiPredicate<Sb, Event> postExpiryTest = (__, ___) -> false;
        private BiPredicate<Sb, Event> preExpiryTest = (__, ___) -> false;

        @Override
        public Predicate<Event> filter() {
            return filter;
        }

        @Override
        public BiPredicate<Sb, Event> midExpiryTest() {
            return midExpiryTest;
        }

        @Override
        public BiPredicate<Sb, Event> postExpiryTest() {
            return postExpiryTest;
        }

        @Override
        public BiPredicate<Sb, Event> preExpiryTest() {
            return preExpiryTest;
        }

        @Override
        public final Slf expireIf(final BiPredicate<Sb, Event> predicate, final ExpiryTestStage... testPoints) {
            for (final var testPoint : testPoints) {
                switch (testPoint) {
                    case PRE -> this.preExpiryTest = this.preExpiryTest.and(predicate);
                    case POST_FILTER -> this.midExpiryTest = this.midExpiryTest.and(predicate);
                    case POST_HANDLE -> this.postExpiryTest = this.postExpiryTest.and(predicate);
                }
            }
            return this.self();
        }

        @Override
        public final Slf filter(final Predicate<Event> predicate) {
            this.filter = this.filter.and(predicate);
            return this.self();
        }
    }
}
