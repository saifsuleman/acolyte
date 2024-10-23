package net.odinmc.core.paper.events.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
import java.util.Set;
import java.util.function.BiConsumer;
import net.odinmc.core.common.events.Subscription;
import net.odinmc.core.common.events.SubscriptionBuilder;
import org.jetbrains.annotations.NotNull;

final class ProtocolSubscriptionBuilderImpl
    extends SubscriptionBuilder.Base<PacketEvent, Subscription, ProtocolHandlerList, ProtocolSubscriptionBuilder>
    implements ProtocolSubscriptionBuilder, ProtocolSubscriptionBuilder.Get {

    @NotNull
    private final ListenerPriority priority;

    @NotNull
    private final Set<PacketType> types;

    @NotNull
    private BiConsumer<PacketEvent, Throwable> exceptionConsumer = (__, throwable) -> throwable.printStackTrace();

    ProtocolSubscriptionBuilderImpl(@NotNull final ListenerPriority priority, @NotNull final Set<PacketType> types) {
        this.priority = priority;
        this.types = types;
    }

    @NotNull
    @Override
    public ProtocolHandlerList handlers() {
        return ProtocolHandlerList.simple(this);
    }

    @Override
    public @NotNull ProtocolSubscriptionBuilder exceptionConsumer(@NotNull BiConsumer<PacketEvent, Throwable> consumer) {
        this.exceptionConsumer = consumer;
        return this;
    }

    @Override
    public @NotNull BiConsumer<PacketEvent, Throwable> exceptionConsumer() {
        return this.exceptionConsumer;
    }

    @Override
    public @NotNull ListenerPriority priority() {
        return this.priority;
    }

    @Override
    public @NotNull Set<PacketType> types() {
        return this.types;
    }
}
