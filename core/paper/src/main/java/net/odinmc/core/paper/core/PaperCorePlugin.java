package net.odinmc.core.paper.core;

import net.odinmc.core.common.config.locale.Locale;
import net.odinmc.core.common.module.data.DataModule;
import net.odinmc.core.common.services.Services;
import net.odinmc.core.paper.config.locale.PaperLocale;
import net.odinmc.core.paper.module.data.PaperDataModule;
import net.odinmc.core.paper.module.redirect.PaperRedirectModule;
import net.odinmc.core.paper.plugin.ExtendedJavaPlugin;
import net.odinmc.core.paper.scoreboard.impl.ProtocolScoreboard;
import net.odinmc.core.paper.scoreboard.interfaces.PaperScoreboard;
import net.odinmc.core.paper.store.AsyncPlayerStore;

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

        Services.getOrProvide(AsyncPlayerStore.class).bindModuleWith(this);

        Services.provideApi(new PaperLocale(), Locale.class);
    }
}
