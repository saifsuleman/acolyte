package net.saifs.odinmc.events.single;

import java.util.function.BiConsumer;
import net.saifs.odinmc.events.Subscription;
import net.saifs.odinmc.events.SubscriptionBuilder;

public interface SingleSubscriptionBuilder<Event>
    extends SubscriptionBuilder<Event, Subscription, SingleHandlerList<Event>, SingleSubscriptionBuilder<Event>> {
    
    static <Event, Priority> SingleSubscriptionBuilder<Event> newBuilder( final Class<Event> eventClass,  final Priority priority) {
        return new SingleSubscriptionBuilderImpl<>(eventClass, priority);
    }

    
    SingleSubscriptionBuilder<Event> exceptionConsumer( BiConsumer<Event, Throwable> consumer);

    
    SingleSubscriptionBuilder<Event> handleSubclasses();

    
    @Override
    default SingleSubscriptionBuilder<Event> self() {
        return this;
    }

    interface Get<Event, Priority> extends SubscriptionBuilder.Get<Event, Subscription> {
        
        Class<Event> eventClass();

        
        BiConsumer<Event, Throwable> exceptionConsumer();

        boolean isHandleSubclasses();

        
        Priority priority();
    }
}
