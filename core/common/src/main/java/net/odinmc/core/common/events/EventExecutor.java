package net.odinmc.core.common.events;

public interface EventExecutor<Event> {
    Class<? extends Event> eventClass();

    void execute(Event event);

    Object nativeExecutor();

    void nativeExecutor(Object nativeExecutor);
}
