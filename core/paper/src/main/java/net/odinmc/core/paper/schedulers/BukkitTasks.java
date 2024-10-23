package net.odinmc.core.paper.schedulers;

import com.google.common.base.Preconditions;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import net.odinmc.core.common.scheduling.Internal;
import net.odinmc.core.common.scheduling.Scheduler;
import net.odinmc.core.common.scheduling.SchedulerProvider;
import net.odinmc.core.common.terminable.Terminable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class BukkitTasks {

    private static final Scheduler ASYNC_SCHEDULER = new BukkitAsyncScheduler();

    private static final AtomicReference<Plugin> PLUGIN = new AtomicReference<>();

    private static final Scheduler SYNC_SCHEDULER = new BukkitSyncScheduler();

    private BukkitTasks() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    @NotNull
    public static Terminable init(@NotNull final Plugin plugin) {
        Preconditions.checkState(Bukkit.getServer().isPrimaryThread(), "Please use #init(Plugin) method in a main thread!");
        BukkitTasks.PLUGIN.set(plugin);
        return Internal.init(SchedulerProvider.of(BukkitTasks.ASYNC_SCHEDULER, BukkitTasks.SYNC_SCHEDULER), new BukkitLogger());
    }

    @NotNull
    static Plugin plugin() {
        return Objects.requireNonNull(BukkitTasks.PLUGIN.get(), "init task first!");
    }
}
