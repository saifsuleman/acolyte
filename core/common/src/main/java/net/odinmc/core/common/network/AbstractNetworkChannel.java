package net.odinmc.core.common.network;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractNetworkChannel<T> implements NetworkChannel<T> {

    private final String name;
    private final Class<T> messageClass;
    private final Set<NetworkChannelListener<T>> listeners = new HashSet<>();

    public AbstractNetworkChannel(String name, Class<T> messageClass) {
        this.name = name;
        this.messageClass = messageClass;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void emit(String sender, T message) {
        for (var listener : listeners) {
            try {
                listener.onMessage(sender, message);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    @Override
    public void addListener(NetworkChannelListener<T> listener) {
        listeners.add(listener);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<T> getMessageClass() {
        return messageClass;
    }
}
