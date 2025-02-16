package net.odinmc.core.paper.core;

import net.odinmc.core.common.events.Plugins;
import net.odinmc.core.common.network.Network;
import net.odinmc.core.common.services.Services;
import net.odinmc.core.common.terminable.TerminableConsumer;
import net.odinmc.core.common.terminable.module.TerminableModule;
import net.odinmc.core.paper.events.PaperEventManager;
import net.odinmc.core.paper.network.PaperRedisNetwork;
import net.odinmc.core.paper.schedulers.BukkitTasks;
import org.redisson.Redisson;
import org.redisson.client.codec.StringCodec;

public class InitiatorModule implements TerminableModule {

    @Override
    public void setup(TerminableConsumer consumer) {
        var plugin = Services.load(PaperCorePlugin.class);
        var config = plugin.configManager().initConfig(Config.class);
        BukkitTasks.init(plugin).bindWith(consumer);
        Plugins.init(this, new PaperEventManager());
        var networkConfig = config.getModules().getNetwork();
        var redisConfig = new org.redisson.config.Config();
        redisConfig.useSingleServer().setAddress(networkConfig.getRedisHost());
        redisConfig.setCodec(new StringCodec());
        var redis = Redisson.create(redisConfig);
        var network = new PaperRedisNetwork(redis);
        Services.provideApi(network, Network.class).bindModuleWith(consumer);
    }
}
