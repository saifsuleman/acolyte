package net.saifs.odinmc.core.paper.events.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
import java.util.Set;
import java.util.function.BiConsumer;
import net.odinmc.core.common.events.Subscription;
import net.odinmc.core.common.events.SubscriptionBuilder;
import org.jetbrains.annotations.NotNull;

public interface ProtocolSubscriptionBuilder
    extends SubscriptionBuilder<PacketEvent, Subscription, ProtocolHandlerList, ProtocolSubscriptionBuilder> {
    @NotNull
    static ProtocolSubscriptionBuilder newBuilder(@NotNull final ListenerPriority priority, @NotNull final PacketType... packets) {
        return new ProtocolSubscriptionBuilderImpl(priority, Set.of(packets));
    }

    @NotNull
    ProtocolSubscriptionBuilder exceptionConsumer(@NotNull BiConsumer<PacketEvent, Throwable> consumer);

    @NotNull
    @Override
    default ProtocolSubscriptionBuilder self() {
        return this;
    }

    interface Get extends SubscriptionBuilder.Get<PacketEvent, Subscription> {
        @NotNull
        BiConsumer<PacketEvent, Throwable> exceptionConsumer();

        @NotNull
        ListenerPriority priority();

        @NotNull
        Set<PacketType> types();
    }
}
