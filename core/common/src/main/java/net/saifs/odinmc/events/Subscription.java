package net.saifs.odinmc.events;

import net.saifs.odinmc.terminable.Terminable;

public interface Subscription extends Terminable {
    boolean active();

    long callCounter();

    @Override
    default void close() {
        this.unregister();
    }

    void unregister();
}
