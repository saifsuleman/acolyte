package net.odinmc.core.common.config;

import java.nio.file.Path;
import net.odinmc.core.common.terminable.Terminable;
import net.odinmc.core.common.terminable.composite.CompositeTerminable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigManager implements Terminable {

    private static final Logger LOGGER = LoggerFactory.getLogger("Commons/ConfigManager");

    private final CompositeTerminable registry = CompositeTerminable.create();
    private final Path folder;

    public ConfigManager(Path folder) {
        this.folder = folder;

        if (!this.folder.toFile().exists()) {
            var ignored = this.folder.toFile().mkdirs();
        }
    }

    public <T> T initConfig(Class<T> config) {
        return initConfig(folder, config);
    }

    private <T> T initConfig(Path dir, Class<T> config) {
        LOGGER.info("Initialising Configuration: {}", config.getSimpleName());
        String fileName = config.getSimpleName().toLowerCase() + ".json";

        final ConfigHandler<T> configHandler = new ConfigHandler<>(dir, fileName, config);
        registry.bind(configHandler);
        return configHandler.getConfig();
    }

    @Override
    public void close() {
        this.registry.closeAndReportException();
    }
}
