package net.saifs.odinmc.core.paper.schedulers;

import lombok.extern.log4j.Log4j2;
import net.odinmc.core.common.scheduling.Logger;
import org.jetbrains.annotations.NotNull;

@Log4j2
final class BukkitLogger implements Logger {

    @Override
    public void severe(@NotNull final String message, @NotNull final Throwable cause) {
        BukkitLogger.log.fatal(message, cause);
    }
}
