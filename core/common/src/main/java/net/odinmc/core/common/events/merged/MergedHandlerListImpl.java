package net.odinmc.core.common.events.merged;

import net.odinmc.core.common.events.FunctionalHandlerList;
import net.odinmc.core.common.events.Subscription;

final class MergedHandlerListImpl<Event, Priority, Handled>
    extends FunctionalHandlerList.Base<Handled, Subscription, MergedHandlerList<Event, Handled>>
    implements MergedHandlerList<Event, Handled> {

    private final MergedSubscriptionBuilder.Get<Event, Priority, Handled> getter;

    MergedHandlerListImpl(final MergedSubscriptionBuilder.Get<Event, Priority, Handled> getter) {
        this.getter = getter;
    }

    @Override
    public Subscription register() {
        return new EventListener<>(this.getter, this.handler).register();
    }
}
