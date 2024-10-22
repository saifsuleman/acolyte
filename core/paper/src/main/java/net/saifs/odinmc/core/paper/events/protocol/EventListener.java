package net.saifs.odinmc.core.paper.events.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.odinmc.core.common.events.Plugins;
import net.odinmc.core.common.events.Subscription;
import org.jetbrains.annotations.NotNull;

final class EventListener extends PacketAdapter implements Subscription {

    private final AtomicBoolean active = new AtomicBoolean(true);

    private final AtomicLong callCount = new AtomicLong();

    @NotNull
    private final BiConsumer<PacketEvent, Throwable> exceptionConsumer;

    @NotNull
    private final Predicate<PacketEvent> filter;

    @NotNull
    private final BiConsumer<Subscription, PacketEvent> handler;

    @NotNull
    private final BiPredicate<Subscription, PacketEvent> midExpiryTest;

    @NotNull
    private final BiPredicate<Subscription, PacketEvent> postExpiryTest;

    @NotNull
    private final BiPredicate<Subscription, PacketEvent> preExpiryTest;

    @NotNull
    private final Set<PacketType> types;

    EventListener(@NotNull final ProtocolSubscriptionBuilder.Get getter, @NotNull final BiConsumer<Subscription, PacketEvent> handler) {
        super(Plugins.plugin(), getter.priority(), getter.types());
        this.types = getter.types();
        this.exceptionConsumer = getter.exceptionConsumer();
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
        Protocol.manager().removePacketListener(this);
    }

    @Override
    public boolean closed() {
        return !this.active.get();
    }

    @Override
    public void onPacketReceiving(@NotNull final PacketEvent event) {
        this.onPacket(event);
    }

    @Override
    public void onPacketSending(@NotNull final PacketEvent event) {
        this.onPacket(event);
    }

    @NotNull
    Subscription register() {
        Protocol.manager().addPacketListener(this);
        return this;
    }

    private void onPacket(@NotNull final PacketEvent event) {
        if (!this.types.contains(event.getPacketType())) {
            return;
        }
        if (!this.active.get()) {
            return;
        }
        if (this.preExpiryTest.test(this, event)) {
            this.unregister();
            return;
        }
        try {
            if (!this.filter.test(event)) {
                return;
            }
            if (this.midExpiryTest.test(this, event)) {
                this.unregister();
                return;
            }
            this.handler.accept(this, event);
            this.callCount.incrementAndGet();
        } catch (final Throwable t) {
            this.exceptionConsumer.accept(event, t);
        }
        if (this.postExpiryTest.test(this, event)) {
            this.unregister();
        }
    }
}
