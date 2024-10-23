package net.odinmc.core.paper.module.data;

import net.odinmc.core.common.module.data.PlayerData;
import net.odinmc.core.paper.events.AsyncDeferredEvent;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.NotNull;

public class AsyncPlayerLoadEvent extends AsyncDeferredEvent {

    private static final HandlerList handlers = new HandlerList();

    private final AsyncPlayerPreLoginEvent loginEvent;
    private final PlayerData playerData;

    public AsyncPlayerLoadEvent(AsyncPlayerPreLoginEvent loginEvent, PlayerData playerData) {
        this.loginEvent = loginEvent;
        this.playerData = playerData;
    }

    public AsyncPlayerPreLoginEvent getLoginEvent() {
        return loginEvent;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
