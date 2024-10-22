package net.saifs.odinmc.core.paper.module;

import net.odinmc.core.common.events.Plugins;
import net.odinmc.core.common.services.Services;
import net.odinmc.core.common.terminable.TerminableConsumer;
import net.odinmc.core.common.terminable.module.TerminableModule;
import net.saifs.odinmc.core.paper.events.PaperEventManager;
import net.saifs.odinmc.core.paper.plugin.PaperCorePlugin;
import net.saifs.odinmc.core.paper.schedulers.BukkitTasks;
import net.saifs.odinmc.core.paper.store.AsyncPlayerStore;

public class InitiatorModule implements TerminableModule {

    @Override
    public void setup(TerminableConsumer consumer) {
        var plugin = Services.load(PaperCorePlugin.class);
        BukkitTasks.init(plugin).bindWith(consumer);
        Plugins.init(plugin, new PaperEventManager());
        Services.getOrProvide(AsyncPlayerStore.class).bindModuleWith(consumer);
    }
}
