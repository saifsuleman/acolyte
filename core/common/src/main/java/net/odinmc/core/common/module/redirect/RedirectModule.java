package net.odinmc.core.common.module.redirect;

import net.odinmc.core.common.network.Network;
import net.odinmc.core.common.network.NetworkChannel;
import net.odinmc.core.common.services.Services;
import net.odinmc.core.common.terminable.TerminableConsumer;
import net.odinmc.core.common.terminable.module.TerminableModule;

import java.util.HashMap;
import java.util.Map;

public abstract class RedirectModule<P> implements TerminableModule {
    protected static final int LOOP_THRESHOLD = 3;

    protected final Network network = Services.load(Network.class);

    protected NetworkChannel<RedirectRequestMessage> requestChannel;
    protected NetworkChannel<RedirectKickMessage> kickChannel;

    @Override
    public void setup(TerminableConsumer consumer) {
        this.requestChannel = network.getChannel(RedirectRequestMessage.class);
        this.kickChannel = network.getChannel(RedirectKickMessage.class);
    }

    public void redirect(P player, String server, RedirectKickCallback kickCallback) {
        redirect(player, server, new HashMap<>(), kickCallback, 0);
    }

    public void redirect(P player, String server, Map<String, Object> additionalData, RedirectKickCallback kickCallback) {
        redirect(player, server, additionalData, kickCallback, 0);
    }

    public abstract void redirect(P player, String server, Map<String, Object> additionalData, RedirectKickCallback kickCallback, int loop);

    protected abstract void onRedirectMessage(String server, RedirectRequestMessage message);

    protected abstract void onKickMessage(String server, RedirectKickMessage message);
}
