package net.saifs.odinmc.core.paper.schedulers;

import net.odinmc.core.common.scheduling.Logger;
import org.jetbrains.annotations.NotNull;

final class BukkitLogger implements Logger {

    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(BukkitLogger.class);

    @Override
    public void severe(@NotNull final String message, @NotNull final Throwable cause) {
        BukkitLogger.log.fatal(message, cause);
    }
}
