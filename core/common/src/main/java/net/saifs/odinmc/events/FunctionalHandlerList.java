package net.saifs.odinmc.events;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.saifs.odinmc.definitions.Self;

public interface FunctionalHandlerList<Event, Sb extends Subscription, Slf extends FunctionalHandlerList<Event, Sb, Slf>> extends Self<Slf> {
    Slf biConsumer(BiConsumer<Sb, Event> handler);

    default Slf consumer(final Consumer<Event> handler) {
        return this.biConsumer((__, e) -> handler.accept(e));
    }

    Sb register();

    abstract class Base<Event, Sb extends Subscription, Slf extends FunctionalHandlerList<Event, Sb, Slf>>
        implements FunctionalHandlerList<Event, Sb, Slf> {

        protected BiConsumer<Sb, Event> handler = (sb, event) -> {};

        @Override
        public final Slf biConsumer(final BiConsumer<Sb, Event> handler) {
            this.handler = this.handler.andThen(handler);
            return this.self();
        }
    }
}
