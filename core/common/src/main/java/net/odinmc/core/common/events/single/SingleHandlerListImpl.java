package net.odinmc.core.common.events.single;

import net.odinmc.core.common.events.FunctionalHandlerList;
import net.odinmc.core.common.events.Subscription;

final class SingleHandlerListImpl<Event, Priority>
    extends FunctionalHandlerList.Base<Event, Subscription, SingleHandlerList<Event>>
    implements SingleHandlerList<Event> {

    private final SingleSubscriptionBuilder.Get<Event, Priority> getter;

    SingleHandlerListImpl(final SingleSubscriptionBuilder.Get<Event, Priority> getter) {
        this.getter = getter;
    }

    @Override
    public Subscription register() {
        return new EventListener<>(this.getter, this.handler).register();
    }
}
