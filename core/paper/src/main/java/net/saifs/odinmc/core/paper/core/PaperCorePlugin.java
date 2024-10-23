package net.saifs.odinmc.core.paper.core;

import net.odinmc.core.common.annotations.Modules;
import net.odinmc.core.common.module.data.DataModule;
import net.odinmc.core.common.services.Services;
import net.saifs.odinmc.core.paper.core.module.InitiatorModule;
import net.saifs.odinmc.core.paper.core.module.data.PaperDataModule;
import net.saifs.odinmc.core.paper.core.module.redirect.PaperRedirectModule;
import net.saifs.odinmc.core.paper.scoreboard.impl.ProtocolScoreboard;
import net.saifs.odinmc.core.paper.scoreboard.interfaces.PaperScoreboard;
import net.saifs.odinmc.core.paper.plugin.ExtendedJavaPlugin;
import net.saifs.odinmc.core.paper.store.AsyncPlayerStore;

@Modules({AsyncPlayerStore.class})
public class PaperCorePlugin extends ExtendedJavaPlugin {
    @Override
    protected void load() {
        Services.getOrProvide(InitiatorModule.class).bindModuleWith(this);
    }

    @Override
    protected void enable() {
        Services.provideApi(new ProtocolScoreboard(), PaperScoreboard.class);
        Services.provideApi(new PaperDataModule(), DataModule.class).bindModuleWith(this);
        Services.provideApi(new PaperRedirectModule(), PaperRedirectModule.class).bindModuleWith(this);
    }
}
