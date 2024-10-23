package net.saifs.odinmc.core.paper.module.data;

import net.odinmc.core.common.module.data.PlayerData;
import net.saifs.odinmc.core.paper.events.AsyncDeferredEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AsyncPlayerSaveEvent extends AsyncDeferredEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final PlayerData playerData;

    public AsyncPlayerSaveEvent(Player player, PlayerData playerData) {
        this.player = player;
        this.playerData = playerData;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
