package net.odinmc.core.common.events.merged;

import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("unchecked")
final class MergedHandlerMappingImpl<Merged, Priority, Handled> implements MergedHandlerMapping<Merged, Priority, Handled> {

    private final Class<Merged> handledClass;
    private final BiConsumer<Merged, Throwable> exceptionConsumer;
    private final Function<Object, Handled> mapping;
    private final Priority priority;

    MergedHandlerMappingImpl(
        final Class<Merged> handledClass,
        final Priority priority,
        final Function<Merged, Handled> mapping,
        final BiConsumer<Merged, Throwable> exceptionConsumer
    ) {
        this.handledClass = handledClass;
        this.priority = priority;
        this.mapping = o -> mapping.apply((Merged) o);
        this.exceptionConsumer = exceptionConsumer;
    }

    public Class<Merged> handledClass() {
        return handledClass;
    }

    @Override
    public void failed(final Merged event, final Throwable error) {
        this.exceptionConsumer.accept(event, error);
    }

    @Override
    public Handled map(final Object object) {
        return this.mapping.apply(object);
    }

    @Override
    public Priority priority() {
        return priority;
    }
}
