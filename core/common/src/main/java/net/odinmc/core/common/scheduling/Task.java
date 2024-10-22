package net.odinmc.core.common.scheduling;

import net.odinmc.core.common.terminable.Terminable;

public interface Task extends Terminable {
    @Override
    default void close() {
        this.stop();
    }

    boolean closed();

    int id();

    boolean stop();

    int timesRan();
}
