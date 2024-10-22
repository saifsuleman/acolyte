package net.odinmc.core.common.events;

import net.odinmc.core.common.terminable.Terminable;

public interface Subscription extends Terminable {
    boolean active();

    boolean closed();

    long callCounter();

    @Override
    default void close() {
        this.unregister();
    }

    void unregister();
}
