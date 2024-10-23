package net.odinmc.core.paper.module.redirect;

import java.util.Map;
import java.util.UUID;
import net.odinmc.core.common.module.redirect.RedirectKickMessage;
import net.odinmc.core.paper.events.AsyncDeferredEvent;
import org.bukkit.event.HandlerList;

public class AsyncRedirectEvent extends AsyncDeferredEvent {

    private static final HandlerList handlers = new HandlerList();

    private boolean allowed = true;
    private String kickMessage;

    private final UUID uuid;
    private final String currentServerName;
    private final Map<String, Object> additionalData;

    public AsyncRedirectEvent(UUID uuid, String currentServerName, Map<String, Object> additionalData) {
        this.uuid = uuid;
        this.currentServerName = currentServerName;
        this.additionalData = additionalData;
    }

    public void allow() {
        allowed = true;
    }

    public void disallow(String message) {
        allowed = false;
        kickMessage = message;
    }

    public void redirect(String server) {
        disallow(RedirectKickMessage.REDIRECT_PREFIX + server);
    }

    public boolean isAllowed() {
        return allowed;
    }

    public String getKickMessage() {
        return kickMessage;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getCurrentServerName() {
        return currentServerName;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
