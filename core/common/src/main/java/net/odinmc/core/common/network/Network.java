package net.odinmc.core.common.network;

import java.util.UUID;

public interface Network {

  void redirect(String server, UUID player);

  default NetworkChannel<String> getChannel(String name) {
    return getChannel(name, String.class);
  }

  <T> NetworkChannel<T> getChannel(String name, Class<T> messageClass);

  default <T> NetworkChannel<T> getChannel(Class<T> messageClass) {
    return getChannel(messageClass.getName(), messageClass);
  }

  String getName();

  String getServerName();
}
