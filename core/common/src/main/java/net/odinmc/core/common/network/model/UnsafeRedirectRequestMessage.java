package net.odinmc.core.common.network.model;

import java.util.UUID;

public record UnsafeRedirectRequestMessage(UUID uuid, String destination) {
}
