package net.saifs.odinmc.events.single;

import net.saifs.odinmc.events.FunctionalHandlerList;
import net.saifs.odinmc.events.Subscription;

final class SingleHandlerListImpl<Event, Priority>
    extends FunctionalHandlerList.Base<Event, Subscription, SingleHandlerList<Event>>
    implements SingleHandlerList<Event> {

    
    private final SingleSubscriptionBuilder.Get<Event, Priority> getter;

    SingleHandlerListImpl( final SingleSubscriptionBuilder.Get<Event, Priority> getter) {
        this.getter = getter;
    }

    
    @Override
    public Subscription register() {
        return new EventListener<>(this.getter, this.handler).register();
    }
}
