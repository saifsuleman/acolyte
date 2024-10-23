package net.saifs.odinmc.core.paper.plugin;

import net.odinmc.core.common.annotations.Configurations;
import net.odinmc.core.common.annotations.Modules;
import net.odinmc.core.common.config.ConfigManager;
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
        var configs = getClass().getDeclaredAnnotationsByType(Configurations.class);
        var modules = getClass().getDeclaredAnnotationsByType(Modules.class);

        for (var config : configs) {
            for (var entry : config.value()) {
                configManager.initConfig(entry);
            }
        }

        for (var module : modules) {
            for (var entry : module.value()) {
                var m = Services.getOrProvide(entry);
                m.bindModuleWith(this);
            }
        }

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
