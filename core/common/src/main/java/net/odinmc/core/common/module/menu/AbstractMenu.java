package net.odinmc.core.common.module.menu;

public abstract class AbstractMenu implements Menu {

    protected final MenuType type;
    protected final String title;
    protected final int refreshInterval;

    public AbstractMenu(MenuType type, String title, int refreshInterval) {
        this.type = type;
        this.title = title;
        this.refreshInterval = refreshInterval;
    }

    @Override
    public MenuType getType() {
        return type;
    }

    @Override
    public int getRefreshInterval() {
        return refreshInterval;
    }
}
