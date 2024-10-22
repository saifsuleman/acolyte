package net.odinmc.core.common.events.merged;

import net.odinmc.core.common.events.FunctionalHandlerList;
import net.odinmc.core.common.events.Subscription;

public interface MergedHandlerList<Event, Handled> extends FunctionalHandlerList<Handled, Subscription, MergedHandlerList<Event, Handled>> {
    static <Event, Priority, Handled> MergedHandlerList<Event, Handled> simple(final MergedSubscriptionBuilder.Get<Event, Priority, Handled> getter) {
        return new MergedHandlerListImpl<>(getter);
    }

    default MergedHandlerList<Event, Handled> self() {
        return this;
    }
}
