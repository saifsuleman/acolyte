package net.odinmc.core.paper.cloud;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.handler.LifecycleEventHandler;
import io.papermc.paper.plugin.lifecycle.event.handler.configuration.AbstractLifecycleEventHandlerConfiguration;
import io.papermc.paper.plugin.lifecycle.event.handler.configuration.LifecycleEventHandlerConfiguration;
import io.papermc.paper.plugin.lifecycle.event.registrar.Registrar;
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import lombok.SneakyThrows;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.odinmc.core.common.services.Services;
import net.odinmc.core.common.util.ReflectionUtil;
import net.odinmc.core.paper.core.PaperCorePlugin;

@SuppressWarnings("ALL")
public class CloudBootstrapHack {

    private final PaperCorePlugin plugin;
    private final Commands commands;
    private final List<LifecycleEventHandler<ReloadableRegistrarEvent<Registrar>>> lifecycleHandlers;
    private final LifecycleEventManager<BootstrapContext> instantLifecycleManager;
    private final BootstrapContext context;

    @SneakyThrows
    public CloudBootstrapHack() {
        plugin = Services.load(PaperCorePlugin.class);
        commands = Services.load(Commands.class);
        lifecycleHandlers = new LinkedList<LifecycleEventHandler<ReloadableRegistrarEvent<Registrar>>>();
        instantLifecycleManager =
            new LifecycleEventManager<BootstrapContext>() {
                @Override
                public <E extends LifecycleEvent> void registerEventHandler(
                    LifecycleEventType<? super BootstrapContext, ? extends E, ?> eventType,
                    LifecycleEventHandler<? super E> eventHandler
                ) {
                    lifecycleHandlers.add((LifecycleEventHandler<ReloadableRegistrarEvent<Registrar>>) eventHandler);
                }

                @Override
                public void registerEventHandler(LifecycleEventHandlerConfiguration<? super BootstrapContext> handlerConfiguration) {
                    lifecycleHandlers.add(((AbstractLifecycleEventHandlerConfiguration) handlerConfiguration).handler());
                }
            };

        context =
            new BootstrapContext() {
                @Override
                public PluginMeta getConfiguration() {
                    return plugin.getPluginMeta();
                }

                @Override
                public Path getDataDirectory() {
                    return plugin.getDataFolder().toPath();
                }

                @Override
                public ComponentLogger getLogger() {
                    return plugin.getComponentLogger();
                }

                @Override
                public Path getPluginSource() {
                    return null;
                }

                @Override
                public LifecycleEventManager<BootstrapContext> getLifecycleManager() {
                    return instantLifecycleManager;
                }

                @Override
                public PluginMeta getPluginMeta() {
                    return plugin.getPluginMeta();
                }
            };
    }

    public BootstrapContext getContext() {
        return context;
    }

    @SneakyThrows
    public void fire() {
        ReflectionUtil.set(commands, "invalid", false);
        var event = new ReloadableRegistrarEvent<>() {
            @Override
            public Registrar registrar() {
                return commands;
            }

            @Override
            public Cause cause() {
                return Cause.RELOAD;
            }
        };
        for (LifecycleEventHandler<ReloadableRegistrarEvent<Registrar>> lifecycleHandler : lifecycleHandlers) {
            lifecycleHandler.run(event);
        }
    }
}
