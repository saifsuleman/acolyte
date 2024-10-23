package net.odinmc.core.common.network.impl;

import net.odinmc.core.common.network.AbstractNetworkChannel;
import net.odinmc.core.common.network.model.RequestMessage;
import net.odinmc.core.common.terminable.Terminable;
import org.redisson.api.RTopic;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RedisNetworkChannel<T> extends AbstractNetworkChannel<T> implements Terminable {
    private final RedisNetwork network;
    private final String name;
    private final RTopic topic;
    private final int listener;

    public RedisNetworkChannel(RedisNetwork network, String name, Class<T> messageClass) {
        super(name, messageClass);

        this.network = network;
        this.name = name;
        this.topic = network.getClient().getTopic(name);

        this.listener = this.topic.addListener(String.class, (channel, msg) -> {
            var message = this.network.getGson().fromJson(msg, RequestMessage.class);

            if (message.network() != null && network.getName() != null
                    && !Objects.equals(message.network(), network.getName())) {
                return;
            }

            var servers = message.servers();
            if (!servers.isEmpty() && !servers.contains(network.getServerName())) {
                return;
            }

            var request = network.getGson().fromJson(message.message(), messageClass);
            this.emit(message.sender(), request);
        });
    }

    @Override
    public void sendTo(String recipient, T message) {
        this.sendToMany(Collections.singletonList(recipient), message);
    }

    @Override
    public void sendToMany(List<String> recipients, T message) {
        var gson = this.network.getGson();
        var request = new RequestMessage(network.getName(), network.getServerName(), recipients, this.name, gson.toJson(message));
        this.broadcast(request);
    }

    @Override
    public void sendToAll(T message) {
        this.sendToMany(Collections.emptyList(), message);
    }

    @Override
    public void close() {
        this.topic.removeListener(this.listener);
    }

    private void broadcast(RequestMessage message) {
        var gson = this.network.getGson();
        this.broadcast(gson.toJson(message));
    }

    private void broadcast(String message) {
        try {
            this.topic.publishAsync(message);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
