package net.saifs.odinmc.events.merged;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import net.saifs.odinmc.events.Subscription;
import net.saifs.odinmc.events.SubscriptionBuilder;

final class MergedSubscriptionBuilderImpl<Event, Priority, Handled>
    extends SubscriptionBuilder.Base<Handled, Subscription, MergedHandlerList<Event, Handled>, MergedSubscriptionBuilder<Event, Priority, Handled>>
    implements MergedSubscriptionBuilder<Event, Priority, Handled>, MergedSubscriptionBuilder.Get<Event, Priority, Handled> {

    private final Map<Class<? extends Event>, MergedHandlerMapping<? extends Event, Priority, Handled>> mappings = new HashMap<>();

    @Override
    public <Merged extends Event> MergedSubscriptionBuilder<Event, Priority, Handled> bindEvent(
        final Class<Merged> cls,
        final Priority priority,
        final Function<Merged, Handled> mapping,
        final BiConsumer<Merged, Throwable> exceptionConsumer
    ) {
        this.mappings.put(cls, new MergedHandlerMappingImpl<>(cls, priority, mapping, exceptionConsumer));
        return this;
    }

    @Override
    public MergedHandlerList<Event, Handled> handlers() {
        if (this.mappings.isEmpty()) {
            throw new IllegalStateException("No mappings were created");
        }
        return MergedHandlerList.simple(this);
    }

    @Override
    public Map<Class<? extends Event>, MergedHandlerMapping<? extends Event, Priority, Handled>> mappings() {
        return mappings;
    }

    @Override
    public BiPredicate<Subscription, Handled> midExpiryTest() {
        return null;
    }

    @Override
    public BiPredicate<Subscription, Handled> postExpiryTest() {
        return null;
    }

    @Override
    public BiPredicate<Subscription, Handled> preExpiryTest() {
        return null;
    }
}
