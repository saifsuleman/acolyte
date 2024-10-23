package net.saifs.odinmc.core.paper.events;

import net.odinmc.core.common.events.merged.MergedSubscriptionBuilder;
import net.odinmc.core.common.events.single.SingleSubscriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

public interface Events {
    @NotNull
    static <Handled> MergedSubscriptionBuilder<Event, EventPriority, Handled> merge(@NotNull final Class<Handled> handledClass) {
        return MergedSubscriptionBuilder.newBuilder(handledClass);
    }

    @NotNull
    @SafeVarargs
    static <Handled extends Event> MergedSubscriptionBuilder<Event, EventPriority, Handled> merge(
        @NotNull final Class<Handled> cls,
        @NotNull final EventPriority priority,
        @NotNull final Class<? extends Handled>... classes
    ) {
        return MergedSubscriptionBuilder.newBuilder(cls, priority, classes);
    }

    @NotNull
    @SafeVarargs
    static <Handled extends Event> MergedSubscriptionBuilder<Event, EventPriority, Handled> merge(
        @NotNull final Class<Handled> cls,
        @NotNull final Class<? extends Handled>... classes
    ) {
        return merge(cls, EventPriority.NORMAL, classes);
    }

    @NotNull
    static <Handled extends Event> SingleSubscriptionBuilder<Handled> subscribe(
        @NotNull final Class<Handled> cls,
        @NotNull final EventPriority priority
    ) {
        return SingleSubscriptionBuilder.newBuilder(cls, priority);
    }

    @NotNull
    static <Handled extends Event> SingleSubscriptionBuilder<Handled> subscribe(@NotNull final Class<Handled> cls) {
        return Events.subscribe(cls, EventPriority.NORMAL);
    }

    static void dispatch(Event event) {
        Bukkit.getServer().getPluginManager().callEvent(event);
    }
}
