package net.odinmc.core.common.network;

import java.util.List;

public interface NetworkChannel<T> {
    void sendTo(String recipient, T message);

    void sendToMany(List<String> recipients, T message);

    void sendToAll(T message);

    void addListener(NetworkChannelListener<T> listener);

    String getName();

    Class<T> getMessageClass();

    void close();
}
