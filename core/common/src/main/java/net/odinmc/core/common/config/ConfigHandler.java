package net.odinmc.core.common.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.odinmc.core.common.services.Services;
import net.odinmc.core.common.terminable.Terminable;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.ValueReference;
import org.spongepowered.configurate.reference.WatchServiceListener;

public class ConfigHandler<T> implements Terminable {

    private WatchServiceListener listener;
    private ConfigurationReference<BasicConfigurationNode> base;
    private ValueReference<T, BasicConfigurationNode> config;

    public ConfigHandler(Path applicationFolder, String configName, Class<T> clazz) {
        Path configFile = Paths.get(applicationFolder + File.separator + configName);

        try {
            if (!configFile.toFile().getParentFile().exists()) {
                var ignored = configFile.toFile().getParentFile().mkdirs();
            }

            this.listener = WatchServiceListener.create();
            this.base =
                this.listener.listenToConfiguration(
                        file -> GsonConfigurationLoader.builder().defaultOptions(opts -> opts.shouldCopyDefaults(true)).path(file).build(),
                        configFile
                    );

            this.listener.listenToFile(
                    configFile,
                    event -> {
                        Services.provide(clazz, getConfig());
                    }
                );

            this.config = this.base.referenceTo(clazz);
            this.base.save();

            Services.provide(clazz, getConfig());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public T getConfig() {
        return config.get();
    }

    @Override
    public void close() throws Exception {
        try {
            this.listener.close();
            this.base.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
