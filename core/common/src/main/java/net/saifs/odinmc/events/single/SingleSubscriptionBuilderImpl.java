package net.saifs.odinmc.events.single;

import java.util.function.BiConsumer;
import net.saifs.odinmc.events.Subscription;
import net.saifs.odinmc.events.SubscriptionBuilder;

final class SingleSubscriptionBuilderImpl<Event, Priority>
    extends SubscriptionBuilder.Base<Event, Subscription, SingleHandlerList<Event>, SingleSubscriptionBuilder<Event>>
    implements SingleSubscriptionBuilder<Event>, SingleSubscriptionBuilder.Get<Event, Priority> {

    private final Class<Event> eventClass;
    private final Priority priority;
    private BiConsumer<Event, Throwable> exceptionConsumer = (__, throwable) -> throwable.printStackTrace();

    private boolean handleSubclasses = false;

    SingleSubscriptionBuilderImpl(final Class<Event> eventClass, final Priority priority) {
        this.eventClass = eventClass;
        this.priority = priority;
    }

    @Override
    public SingleSubscriptionBuilder<Event> exceptionConsumer(BiConsumer<Event, Throwable> consumer) {
        this.exceptionConsumer = consumer;
        return this;
    }

    @Override
    public SingleSubscriptionBuilder<Event> handleSubclasses() {
        this.handleSubclasses = true;
        return this;
    }

    @Override
    public SingleHandlerList<Event> handlers() {
        return SingleHandlerList.simple(this);
    }

    @Override
    public Class<Event> eventClass() {
        return eventClass;
    }

    @Override
    public BiConsumer<Event, Throwable> exceptionConsumer() {
        return exceptionConsumer;
    }

    @Override
    public boolean isHandleSubclasses() {
        return this.handleSubclasses;
    }

    @Override
    public Priority priority() {
        return priority;
    }
}
