package net.saifs.odinmc.core.paper.events;

import net.odinmc.core.common.events.EventExecutor;
import net.odinmc.core.common.events.EventManager;
import net.odinmc.core.common.events.Plugins;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public final class PaperEventManager implements EventManager<Event, EventPriority> {

    @NotNull
    @Override
    public <Registered extends Event> EventExecutor<Registered> register(
        @NotNull final Class<Registered> eventClass,
        @NotNull final EventPriority priority,
        @NotNull final EventExecutor<Registered> executor
    ) {
        final var handler = new Handler<>(executor);
        Bukkit.getPluginManager().registerEvent(eventClass, handler, priority, handler, Plugins.plugin(), false);
        return executor;
    }

    @Override
    public <Registered extends Event> void unregister(@NotNull final EventExecutor<Registered> executor) {
        var listener = (Listener) executor.nativeExecutor();
        HandlerList.unregisterAll(listener);
    }

    private record Handler<Registered>(@NotNull EventExecutor<Registered> executor) implements org.bukkit.plugin.EventExecutor, Listener {
        private Handler {
            executor.nativeExecutor(this);
        }

        @Override
        public void execute(@NotNull final Listener listener, @NotNull final Event event) {
            this.executor.execute((Registered) event);
        }
    }
}
