package net.odinmc.core.paper.core;

import net.odinmc.core.common.annotations.Modules;
import net.odinmc.core.common.module.data.DataModule;
import net.odinmc.core.common.services.Services;
import net.odinmc.core.paper.locale.PaperLocale;
import net.odinmc.core.paper.module.data.PaperDataModule;
import net.odinmc.core.paper.module.redirect.PaperRedirectModule;
import net.odinmc.core.paper.plugin.ExtendedJavaPlugin;
import net.odinmc.core.paper.scoreboard.impl.ProtocolScoreboard;
import net.odinmc.core.paper.scoreboard.interfaces.PaperScoreboard;
import net.odinmc.core.paper.store.AsyncPlayerStore;

@Modules({AsyncPlayerStore.class})
public class PaperCorePlugin extends ExtendedJavaPlugin {

    @Override
    protected void load() {
        Services.getOrProvide(InitiatorModule.class).bindModuleWith(this);
    }

    @Override
    protected void enable() {
        Services.provideApi(new ProtocolScoreboard(), PaperScoreboard.class).bindModuleWith(this);
        Services.provideApi(new PaperDataModule(), DataModule.class).bindModuleWith(this);
        Services.provideApi(new PaperRedirectModule(), PaperRedirectModule.class).bindModuleWith(this);

        Services.provide(PaperLocale.class, new PaperLocale());
    }
}
