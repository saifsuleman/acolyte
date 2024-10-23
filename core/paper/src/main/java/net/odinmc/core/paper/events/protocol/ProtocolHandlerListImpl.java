package net.odinmc.core.paper.events.protocol;

import com.comphenix.protocol.events.PacketEvent;
import net.odinmc.core.common.events.FunctionalHandlerList;
import net.odinmc.core.common.events.Subscription;
import org.jetbrains.annotations.NotNull;

final class ProtocolHandlerListImpl
    extends FunctionalHandlerList.Base<PacketEvent, Subscription, ProtocolHandlerList>
    implements ProtocolHandlerList {

    @NotNull
    private final ProtocolSubscriptionBuilder.Get getter;

    ProtocolHandlerListImpl(@NotNull final ProtocolSubscriptionBuilder.Get getter) {
        this.getter = getter;
    }

    @NotNull
    @Override
    public Subscription register() {
        return new EventListener(this.getter, this.handler).register();
    }
}
