package net.saifs.odinmc.events.merged;

import net.saifs.odinmc.events.FunctionalHandlerList;
import net.saifs.odinmc.events.Subscription;

public interface MergedHandlerList<Event, Handled> extends FunctionalHandlerList<Handled, Subscription, MergedHandlerList<Event, Handled>> {
    static <Event, Priority, Handled> MergedHandlerList<Event, Handled> simple(final MergedSubscriptionBuilder.Get<Event, Priority, Handled> getter) {
        return new MergedHandlerListImpl<>(getter);
    }

    default MergedHandlerList<Event, Handled> self() {
        return this;
    }
}
