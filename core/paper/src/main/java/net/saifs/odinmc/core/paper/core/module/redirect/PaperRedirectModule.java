package net.saifs.odinmc.core.paper.core.module.redirect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.odinmc.core.common.module.data.DataModule;
import net.odinmc.core.common.module.redirect.*;
import net.odinmc.core.common.scheduling.Schedulers;
import net.odinmc.core.common.services.Services;
import net.saifs.odinmc.core.paper.events.Events;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

public class PaperRedirectModule extends RedirectModule<Player> {
    private static final int loopThreshold = 3;

    private final Gson gson = new GsonBuilder().create();
    private final Map<UUID, PlayerRedirectCache> redirectCache = new HashMap<>();
    private final Executor executor = Services.getOrProvide(DataModule.class).databaseExecutor();

    public void redirect(Player player, String server, RedirectKickCallback kickCallback) {
        redirect(player, server, null, kickCallback);
    }

    public void redirect(Player player, String server, Map<String, Object> additionalData, RedirectKickCallback kickCallback) {
        redirect(player, server, additionalData, kickCallback, 0);
    }

    public void redirect(Player player, String server, Map<String, Object> additionalData, RedirectKickCallback kickCallback, int loop) {
        var cache = redirectCache.computeIfAbsent(player.getUniqueId(), (ignored) -> new PlayerRedirectCache());

        if (!cache.tryRedirect(server, kickCallback, additionalData)) {
            return;
        }

        Schedulers.async().run(() -> {
            var outboundEvent = new AsyncOutboundRedirectEvent(player, server, additionalData);
            Events.dispatch(outboundEvent);
            outboundEvent.executeDeferred(executor).thenAcceptAsync((success) -> {
                if (!success) {
                    kickCallback.onKick("Something went wrong... (incomplete future)");
                    return;
                }

                var serialized = gson.toJson(additionalData);
                requestChannel.sendTo(server, new RedirectRequestMessage(player.getUniqueId(), outboundEvent.getDestination(), serialized, loop));
            });
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onRedirectMessage(String server, RedirectRequestMessage message) {
        var event = new AsyncRedirectEvent(message.uuid(), server, gson.fromJson(message.additionalData(), Map.class));
        Events.dispatch(event);
        event.executeDeferred(executor).thenAcceptAsync(success -> {
            if (!success) {
                this.kickChannel.sendTo(server, new RedirectKickMessage(event.getUUID(), "Something went wrong processing your redirect.", message.loop()));
                return;
            }

            if (event.isAllowed()) {
                network.redirect(network.getServerName(), message.uuid());
            } else {
                kickChannel.sendTo(server, new RedirectKickMessage(event.getUUID(), event.getKickMessage(), message.loop()));
            }
        });
    }

    @Override
    protected void onKickMessage(String server, RedirectKickMessage message) {
        var player = Bukkit.getServer().getPlayer(message.uuid());
        if (player == null) {
            return;
        }

        var cache = redirectCache.get(player.getUniqueId());
        if (cache == null) {
            return;
        }

        var entry = cache.removeRedirect(server);
        if (entry == null) {
            return;
        }

        if (message.isRedirect()) {
            if (message.loop() < loopThreshold) {
                redirect(player, message.getRedirectServer(), entry.additionalData(), entry.callback(), message.loop() + 1);
            } else {
                entry.callback().onKick("Something went wrong processing your redirect...");
            }

            return;
        }

        entry.callback().onKick(message.kickMessage());
    }
}
