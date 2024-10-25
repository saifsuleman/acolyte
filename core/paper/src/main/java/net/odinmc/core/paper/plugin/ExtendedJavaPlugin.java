package net.odinmc.core.paper.plugin;

import net.odinmc.core.common.config.configurate.ConfigManager;
import net.odinmc.core.common.services.Services;
import net.odinmc.core.common.terminable.TerminableConsumer;
import net.odinmc.core.common.terminable.composite.CompositeTerminable;
import org.bukkit.plugin.java.JavaPlugin;

public class ExtendedJavaPlugin extends JavaPlugin implements TerminableConsumer {

    private CompositeTerminable terminableRegistry;
    private ConfigManager configManager;

    protected void load() {}

    protected void enable() {}

    protected void disable() {}

    @Override
    @SuppressWarnings("unchecked")
    public final void onLoad() {
        this.terminableRegistry = CompositeTerminable.create();
        Services.provide((Class<ExtendedJavaPlugin>) getClass(), this);
        this.configManager = new ConfigManager(getDataFolder().toPath());
        this.configManager.bindWith(this);
        this.load();
    }

    @Override
    public final void onEnable() {
        enable();
    }

    public ConfigManager configManager() {
        return configManager;
    }

    @Override
    public final void onDisable() {
        this.disable();
        terminableRegistry.closeAndReportException();
    }

    @Override
    public <T extends AutoCloseable> T bind(T terminable) {
        return terminableRegistry.bind(terminable);
    }
}
