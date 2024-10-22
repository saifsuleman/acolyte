package net.saifs.odinmc.core.paper.events.protocol;

import com.comphenix.protocol.events.PacketEvent;
import net.odinmc.core.common.events.FunctionalHandlerList;
import net.odinmc.core.common.events.Subscription;
import org.jetbrains.annotations.NotNull;

public interface ProtocolHandlerList extends FunctionalHandlerList<PacketEvent, Subscription, ProtocolHandlerList> {
    @NotNull
    static ProtocolHandlerList simple(@NotNull final ProtocolSubscriptionBuilder.Get getter) {
        return new ProtocolHandlerListImpl(getter);
    }

    @Override
    @NotNull
    default ProtocolHandlerList self() {
        return this;
    }
}
