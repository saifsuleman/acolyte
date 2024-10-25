package net.odinmc.core.common.module.menu;

public enum MenuType {
    CHEST,
    DROPPER,
    HOPPER,
    PLAYER;

    public static MenuType getByName(String name) {
        return valueOf(name.toUpperCase());
    }
}
