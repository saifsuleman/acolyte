package net.saifs.odinmc.events.merged;

import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.saifs.odinmc.events.Subscription;
import net.saifs.odinmc.events.SubscriptionBuilder;

public interface MergedSubscriptionBuilder<Event, Priority, Handled>
    extends SubscriptionBuilder<Handled, Subscription, MergedHandlerList<Event, Handled>, MergedSubscriptionBuilder<Event, Priority, Handled>> {
    
    static <Event, Priority, Handled> MergedSubscriptionBuilder<Event, Priority, Handled> newBuilder( final Class<Handled> handledClass) {
        return new MergedSubscriptionBuilderImpl<>(handledClass);
    }

    
    @SafeVarargs
    static <Event, Priority, Handled extends Event> MergedSubscriptionBuilder<Event, Priority, Handled> newBuilder(
         final Class<Handled> cls,
         final Priority priority,
         final Class<? extends Handled>... classes
    ) {
        Preconditions.checkArgument(classes.length >= 2, "merge method used for only one subclass");
        final var builder = MergedSubscriptionBuilder.<Event, Priority, Handled>newBuilder(cls);
        for (final var event : classes) {
            builder.bindEvent(event, priority, e -> e);
        }
        return builder;
    }

    
    default <Merged extends Event> MergedSubscriptionBuilder<Event, Priority, Handled> bindEvent(
         final Class<Merged> cls,
         final Priority priority,
         final Function<Merged, Handled> mapping
    ) {
        return this.bindEvent(cls, priority, mapping, (event, throwable) -> throwable.printStackTrace());
    }

    
    <Merged extends Event> MergedSubscriptionBuilder<Event, Priority, Handled> bindEvent(
         Class<Merged> cls,
         Priority priority,
         Function<Merged, Handled> mapping,
         BiConsumer<Merged, Throwable> exceptionConsumer
    );

    
    @Override
    default MergedSubscriptionBuilder<Event, Priority, Handled> self() {
        return this;
    }

    interface Get<Event, Priority, Handled> extends SubscriptionBuilder.Get<Handled, Subscription> {
        
        Map<Class<? extends Event>, MergedHandlerMapping<? extends Event, Priority, Handled>> mappings();
    }
}
