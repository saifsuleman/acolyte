package net.saifs.odinmc.core.paper.plugin;

import net.odinmc.core.common.annotations.Modules;
import net.odinmc.core.common.services.Services;
import net.odinmc.core.common.terminable.composite.CompositeTerminable;
import org.bukkit.plugin.java.JavaPlugin;

public class ExtendedJavaPlugin extends JavaPlugin {

    private CompositeTerminable terminableRegistry;

    protected void load() {}

    protected void enable() {}

    protected void disable() {}

    @Override
    @SuppressWarnings("unchecked")
    public final void onLoad() {
        this.terminableRegistry = CompositeTerminable.create();
        Services.provide((Class<ExtendedJavaPlugin>) getClass(), this);
        this.load();
    }

    @Override
    public final void onEnable() {
        for (var annotation : this.getClass().getDeclaredAnnotations()) {
            if (annotation instanceof Modules modules) {
                for (var entry : modules.value()) {
                    var module = Services.getOrProvide(entry);
                    module.bindModuleWith(terminableRegistry);
                }
            }
        }

        enable();
    }

    @Override
    public final void onDisable() {
        this.disable();
        terminableRegistry.closeAndReportException();
    }
}
