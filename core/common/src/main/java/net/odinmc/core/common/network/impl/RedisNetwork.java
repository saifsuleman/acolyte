package net.odinmc.core.common.network.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.odinmc.core.common.network.AbstractNetwork;
import net.odinmc.core.common.network.NetworkChannel;
import net.odinmc.core.common.terminable.TerminableConsumer;
import net.odinmc.core.common.terminable.module.TerminableModule;
import org.redisson.api.RedissonClient;

public abstract class RedisNetwork extends AbstractNetwork implements TerminableModule {

    protected final RedissonClient client;
    protected final Gson gson;

    public RedisNetwork(RedissonClient client) {
        this.client = client;
        this.gson = new GsonBuilder().create();
    }

    @Override
    public void setup(TerminableConsumer consumer) {
        consumer.bind(this::unregister);
    }

    public Gson getGson() {
        return gson;
    }

    public RedissonClient getClient() {
        return client;
    }

    @Override
    protected <T> NetworkChannel<T> newChannel(String name, Class<T> messageClass) {
        return new RedisNetworkChannel<>(this, name, messageClass);
    }

    private void unregister() {
        for (var channel : channels.values()) {
            channel.close();
        }
    }
}
