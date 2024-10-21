package net.saifs.odinmc.events;

public interface EventManager<Event, Priority> {
    <Registered extends Event> EventExecutor<Registered> register(
        Class<Registered> eventClass,
        Priority priority,
        EventExecutor<Registered> executor
    );

    <Registered extends Event> void unregister(EventExecutor<Registered> executor);
}
