package net.odinmc.core.common.network;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public abstract class AbstractNetwork implements Network {

  protected final Map<String, NetworkChannel<?>> channels = new HashMap<>();

  @Override
  public <T> NetworkChannel<T> getChannel(String name, Class<T> messageClass) {
    var channel = (NetworkChannel<T>) channels.get(name);
    if (channel == null) {
      channel = newChannel(name, messageClass);
      channels.put(name, channel);
    }
    return channel;
  }

  protected abstract <T> NetworkChannel<T> newChannel(String name, Class<T> messageClass);

  protected Map<String, NetworkChannel<?>> getChannels() {
    return channels;
  }
}
