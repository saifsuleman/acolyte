package net.odinmc.core.common.network;

public interface NetworkChannelListener<T> {
    void onMessage(String sender, T message);
}
