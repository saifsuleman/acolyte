package net.odinmc.core.paper.network;

import java.util.UUID;
import net.odinmc.core.common.network.NetworkChannel;
import net.odinmc.core.common.network.impl.RedisNetwork;
import net.odinmc.core.common.network.model.UnsafeRedirectRequestMessage;
import net.odinmc.core.common.ref.Ref;
import net.odinmc.core.common.services.Services;
import net.odinmc.core.paper.core.Config;
import org.redisson.api.RedissonClient;

public class PaperRedisNetwork extends RedisNetwork {

    private final Ref<Config> config = Services.ref(Config.class);
    private final NetworkChannel<UnsafeRedirectRequestMessage> redirectChannel;

    public PaperRedisNetwork(RedissonClient client) {
        super(client);
        this.redirectChannel = this.getChannel(UnsafeRedirectRequestMessage.class);
    }

    @Override
    public void redirect(String server, UUID player) {
        this.redirectChannel.sendTo(getName(), new UnsafeRedirectRequestMessage(player, server));
    }

    @Override
    public String getName() {
        return config.get().getModules().getNetwork().getNetworkName();
    }

    @Override
    public String getServerName() {
        return config.get().getModules().getNetwork().getServerName();
    }
}
