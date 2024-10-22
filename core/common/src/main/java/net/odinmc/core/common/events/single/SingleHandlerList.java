package net.odinmc.core.common.events.single;

import net.odinmc.core.common.events.FunctionalHandlerList;
import net.odinmc.core.common.events.Subscription;

public interface SingleHandlerList<Event> extends FunctionalHandlerList<Event, Subscription, SingleHandlerList<Event>> {
    static <Event, Priority> SingleHandlerList<Event> simple(final SingleSubscriptionBuilder.Get<Event, Priority> getter) {
        return new SingleHandlerListImpl<>(getter);
    }

    @Override
    default SingleHandlerList<Event> self() {
        return this;
    }
}
