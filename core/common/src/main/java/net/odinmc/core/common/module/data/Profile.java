package net.odinmc.core.common.module.data;

import java.util.List;
import java.util.UUID;

public record Profile(
        UUID id,
        String name,
        List<Property> properties,
        long lastUpdateTime
) {
    public boolean completed(boolean signature) {
        if (id == null) {
            return false;
        }

        if (name == null || name.isEmpty()) {
            return false;
        }

        if (properties.isEmpty()) {
            return false;
        }

        var textures = false;
        for (var property : properties) {
            if (!property.name().equals("textures")) {
                continue;
            }

            textures = !signature || property.signature() != null;
            break;
        }

        return textures;
    }

    public record Property(
            String name,
            String value,
            String signature
    ) {}
}
