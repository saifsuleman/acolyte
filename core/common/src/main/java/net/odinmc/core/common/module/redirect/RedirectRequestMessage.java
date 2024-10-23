package net.odinmc.core.common.module.redirect;

import java.util.UUID;

public record RedirectRequestMessage(UUID uuid, String destination, String additionalData, int loop) {}
