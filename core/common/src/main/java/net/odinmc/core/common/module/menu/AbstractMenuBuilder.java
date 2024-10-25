package net.odinmc.core.common.module.menu;

public abstract class AbstractMenuBuilder implements MenuBuilder {

    protected final MenuType type;
    protected String title;
    protected int refreshInterval;

    protected AbstractMenuBuilder(MenuType type) {
        this.type = type;
    }

    @Override
    public MenuBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public MenuBuilder setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
        return this;
    }
}
