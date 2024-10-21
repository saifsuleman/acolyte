package net.saifs.odinmc.events.single;

import net.saifs.odinmc.events.FunctionalHandlerList;
import net.saifs.odinmc.events.Subscription;

public interface SingleHandlerList<Event> extends FunctionalHandlerList<Event, Subscription, SingleHandlerList<Event>> {
    
    static <Event, Priority> SingleHandlerList<Event> simple( final SingleSubscriptionBuilder.Get<Event, Priority> getter) {
        return new SingleHandlerListImpl<>(getter);
    }

    @Override
    default SingleHandlerList<Event> self() {
        return this;
    }
}
