package net.saifs.odinmc.core.paper.schedulers;

import com.google.common.base.Preconditions;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import lombok.experimental.UtilityClass;
import net.odinmc.core.common.terminable.Terminable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import net.odinmc.core.common.scheduling.Internal;
import net.odinmc.core.common.scheduling.Scheduler;
import net.odinmc.core.common.scheduling.SchedulerProvider;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class BukkitTasks {

    private final Scheduler ASYNC_SCHEDULER = new BukkitAsyncScheduler();

    private final AtomicReference<Plugin> PLUGIN = new AtomicReference<>();

    private final Scheduler SYNC_SCHEDULER = new BukkitSyncScheduler();

    @NotNull
    public Terminable init(@NotNull final Plugin plugin) {
        Preconditions.checkState(Bukkit.getServer().isPrimaryThread(), "Please use #init(Plugin) method in a main thread!");
        BukkitTasks.PLUGIN.set(plugin);
        return Internal.init(SchedulerProvider.of(BukkitTasks.ASYNC_SCHEDULER, BukkitTasks.SYNC_SCHEDULER), new BukkitLogger());
    }

    @NotNull
    Plugin plugin() {
        return Objects.requireNonNull(BukkitTasks.PLUGIN.get(), "init task first!");
    }
}
