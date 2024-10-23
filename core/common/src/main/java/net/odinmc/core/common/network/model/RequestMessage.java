package net.odinmc.core.common.network.model;

import java.util.List;

public record RequestMessage(String network, String sender, List<String> servers, String channel, String message) {
}
