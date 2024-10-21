package net.saifs.odinmc.events;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unchecked")
public class Plugins {
    private static final AtomicReference<EventManager<?, ?>> EVENT_MANAGER = new AtomicReference<>();
    private static final AtomicReference<Object> PLUGIN = new AtomicReference<>();

    public static <Event, Priority> void init( final EventManager<Event, Priority> manager) {
        Plugins.EVENT_MANAGER.set(manager);
    }

    public static <Event, Priority> void init(final Object plugin,  final EventManager<Event, Priority> manager) {
        Plugins.PLUGIN.set(plugin);
        Plugins.EVENT_MANAGER.set(manager);
    }

    
    public static <Event, Priority> EventManager<Event, Priority> manager() {
        return (EventManager<Event, Priority>) Objects.requireNonNull(
            Plugins.EVENT_MANAGER.get(),
            "EventManager not found, use #init(Plugin, EventManager) to initialize!"
        );
    }

    
    public static <Plugin> Plugin plugin() {
        return Objects.requireNonNull((Plugin) Plugins.PLUGIN.get(), "Plugin not found, use #init(Plugin, EventManager) to initialize!");
    }
}
