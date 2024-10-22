package net.saifs.odinmc.core.paper.events;

import java.util.function.Consumer;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public class EventHandlers {

    private static final Consumer<? extends Cancellable> SET_CANCELLED = e -> e.setCancelled(true);

    private static final Consumer<? extends Cancellable> UNSET_CANCELLED = e -> e.setCancelled(false);

    @NotNull
    public static <T extends Cancellable> Consumer<T> cancel() {
        return (Consumer<T>) EventHandlers.SET_CANCELLED;
    }

    @NotNull
    public static <T extends Cancellable> Consumer<T> uncancel() {
        return (Consumer<T>) EventHandlers.UNSET_CANCELLED;
    }
}
