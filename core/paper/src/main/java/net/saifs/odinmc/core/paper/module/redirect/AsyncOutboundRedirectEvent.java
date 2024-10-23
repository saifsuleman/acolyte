package net.saifs.odinmc.core.paper.module.redirect;

import java.util.Map;
import net.saifs.odinmc.core.paper.events.AsyncDeferredEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class AsyncOutboundRedirectEvent extends AsyncDeferredEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Map<String, Object> additionalData;

    private boolean cancelled = false;
    private String destination;

    public AsyncOutboundRedirectEvent(Player player, String destination, Map<String, Object> additionalData) {
        this.player = player;
        this.destination = destination;
        this.additionalData = additionalData;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
