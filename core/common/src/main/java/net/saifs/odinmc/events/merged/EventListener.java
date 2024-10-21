package net.saifs.odinmc.events.merged;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.saifs.odinmc.events.EventExecutor;
import net.saifs.odinmc.events.Plugins;
import net.saifs.odinmc.events.Subscription;

@SuppressWarnings("unchecked")
final class EventListener<Event, Priority, Handled> implements Subscription {

    private final AtomicBoolean active = new AtomicBoolean(true);

    private final AtomicLong callCount = new AtomicLong();

    private final Predicate<Handled> filter;

    private final BiConsumer<Subscription, Handled> handler;

    private final Map<Class<? extends Event>, MergedHandlerMapping<? extends Event, Priority, Handled>> mappings;

    private final BiPredicate<Subscription, Handled> midExpiryTest;

    private final BiPredicate<Subscription, Handled> postExpiryTest;

    private final BiPredicate<Subscription, Handled> preExpiryTest;

    private final Collection<EventExecutor<?>> registeredEvents = ConcurrentHashMap.newKeySet();

    EventListener(final MergedSubscriptionBuilder.Get<Event, Priority, Handled> getter, final BiConsumer<Subscription, Handled> handler) {
        this.mappings = Collections.unmodifiableMap(getter.mappings());
        this.filter = getter.filter();
        this.preExpiryTest = getter.preExpiryTest();
        this.midExpiryTest = getter.midExpiryTest();
        this.postExpiryTest = getter.postExpiryTest();
        this.handler = handler;
    }

    @Override
    public boolean active() {
        return this.active.get();
    }

    @Override
    public long callCounter() {
        return this.callCount.get();
    }

    @Override
    public void unregister() {
        if (!this.active.getAndSet(false)) {
            return;
        }
        final var manager = Plugins.manager();
        for (final var event : this.registeredEvents) {
            manager.unregister(event);
        }
    }

    public boolean closed() {
        return !this.active.get();
    }

    Subscription register() {
        final var registered = new IdentityHashMap<Class<?>, Priority>();
        this.mappings.forEach((eventClass, mapping) -> {
                final var priority = mapping.priority();
                final var existing = registered.put(eventClass, priority);
                if (existing != null) {
                    if (existing != priority) {
                        throw new RuntimeException("Unable to register the same event with different priorities: %s".formatted(eventClass));
                    }
                    return;
                }
                final var cls = (Class<Event>) eventClass;
                final var executor = new Executor(cls);
                this.registeredEvents.add(Plugins.manager().register(cls, priority, executor));
            });
        return this;
    }

    private final class Executor implements EventExecutor<Event> {

        private final Class<Event> eventClass;

        private final AtomicReference<Object> nativeExecutor = new AtomicReference<>();

        Executor(final Class<Event> eventClass) {
            this.eventClass = eventClass;
        }

        @Override
        public Class<? extends Event> eventClass() {
            return this.eventClass;
        }

        @Override
        public void execute(final Event event) {
            final var mapping = (MergedHandlerMapping<Event, Priority, Handled>) EventListener.this.mappings.get(event.getClass());
            if (mapping == null) {
                return;
            }
            if (!EventListener.this.active.get()) {
                Plugins.manager().unregister(this);
                return;
            }
            final var handledInstance = mapping.map(event);
            if (EventListener.this.preExpiryTest.test(EventListener.this, handledInstance)) {
                Plugins.manager().unregister(this);
                EventListener.this.active.set(false);
                return;
            }
            try {
                if (!EventListener.this.filter.test(handledInstance)) {
                    return;
                }
                if (EventListener.this.midExpiryTest.test(EventListener.this, handledInstance)) {
                    Plugins.manager().unregister(this);
                    EventListener.this.active.set(false);
                    return;
                }
                EventListener.this.handler.accept(EventListener.this, handledInstance);
                EventListener.this.callCount.incrementAndGet();
            } catch (final Throwable t) {
                mapping.failed(event, t);
            }
            if (EventListener.this.postExpiryTest.test(EventListener.this, handledInstance)) {
                Plugins.manager().unregister(this);
                EventListener.this.active.set(false);
            }
        }

        @Override
        public Object nativeExecutor() {
            return Objects.requireNonNull(this.nativeExecutor.get(), "native executor");
        }

        @Override
        public void nativeExecutor(final Object nativeExecutor) {
            this.nativeExecutor.set(nativeExecutor);
        }
    }
}
