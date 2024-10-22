package net.saifs.odinmc.core.paper.events;

import java.util.function.Predicate;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public class EventFilters {

    private static final Predicate<? extends Cancellable> IGNORE_CANCELLED = e -> !e.isCancelled();
    private static final Predicate<? extends Event> IGNORE_CANCELLED_SOFT = e ->
        !(e instanceof Cancellable cancellable) || !cancellable.isCancelled();

    private static final Predicate<? extends PlayerLoginEvent> IGNORE_DISALLOWED_LOGIN = e -> e.getResult() == PlayerLoginEvent.Result.ALLOWED;

    private static final Predicate<? extends AsyncPlayerPreLoginEvent> IGNORE_DISALLOWED_PRE_LOGIN = e ->
        e.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED;

    private static final Predicate<? extends PlayerMoveEvent> IGNORE_SAME_BLOCK = e ->
        e.getFrom().getBlockX() != e.getTo().getBlockX() ||
        e.getFrom().getBlockZ() != e.getTo().getBlockZ() ||
        e.getFrom().getBlockY() != e.getTo().getBlockY() ||
        !e.getFrom().getWorld().equals(e.getTo().getWorld());

    private static final Predicate<? extends PlayerMoveEvent> IGNORE_SAME_BLOCK_AND_Y = e ->
        e.getFrom().getBlockX() != e.getTo().getBlockX() ||
        e.getFrom().getBlockZ() != e.getTo().getBlockZ() ||
        !e.getFrom().getWorld().equals(e.getTo().getWorld());

    private static final Predicate<? extends PlayerMoveEvent> IGNORE_SAME_CHUNK = e ->
        e.getFrom().getBlockX() >> 4 != e.getTo().getBlockX() >> 4 ||
        e.getFrom().getBlockZ() >> 4 != e.getTo().getBlockZ() >> 4 ||
        !e.getFrom().getWorld().equals(e.getTo().getWorld());

    private static final Predicate<? extends Cancellable> IGNORE_UNCANCELLED = Cancellable::isCancelled;

    @NotNull
    public <T extends Cancellable> Predicate<T> ignoreCancelled() {
        return (Predicate<T>) EventFilters.IGNORE_CANCELLED;
    }

    @NotNull
    public <T extends Event> Predicate<T> ignoreCancelledSoft() {
        return (Predicate<T>) EventFilters.IGNORE_CANCELLED_SOFT;
    }

    @NotNull
    public <T extends PlayerLoginEvent> Predicate<T> ignoreDisallowedLogin() {
        return (Predicate<T>) EventFilters.IGNORE_DISALLOWED_LOGIN;
    }

    @NotNull
    public <T extends AsyncPlayerPreLoginEvent> Predicate<T> ignoreDisallowedPreLogin() {
        return (Predicate<T>) EventFilters.IGNORE_DISALLOWED_PRE_LOGIN;
    }

    @NotNull
    public <T extends Cancellable> Predicate<T> ignoreNotCancelled() {
        return (Predicate<T>) EventFilters.IGNORE_UNCANCELLED;
    }

    @NotNull
    public <T extends PlayerMoveEvent> Predicate<T> ignoreSameBlock() {
        return (Predicate<T>) EventFilters.IGNORE_SAME_BLOCK;
    }

    @NotNull
    public <T extends PlayerMoveEvent> Predicate<T> ignoreSameBlockAndY() {
        return (Predicate<T>) EventFilters.IGNORE_SAME_BLOCK_AND_Y;
    }

    @NotNull
    public <T extends PlayerMoveEvent> Predicate<T> ignoreSameChunk() {
        return (Predicate<T>) EventFilters.IGNORE_SAME_CHUNK;
    }

    @NotNull
    public <T extends PlayerEvent> Predicate<T> playerHasPermission(@NotNull final String permission) {
        return e -> e.getPlayer().hasPermission(permission);
    }
}
