package net.odinmc.core.common.module.level;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.odinmc.core.common.scheduling.Schedulers;
import net.odinmc.core.common.terminable.module.TerminableModule;

public abstract class LevelModule implements TerminableModule {

    private final Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(LevelResource.class, new LevelResourceTypeAdapter(this)).create();
    private final LevelRepository levelRepository = new LevelRepository();
    private final Multimap<String, RegisteredLevelResourceObserver<?>> registeredResourceObservers = HashMultimap.create();
    private final Map<String, Level> levels = new HashMap<>();

    public abstract Point newPoint(double x, double y, double z);

    public abstract Region newRegion(Point pointA, Point pointB);

    public <T extends LevelResource> RegisteredLevelResourceObserver<T> observeResource(
        String worldName,
        String resourceName,
        LevelResourceObserver<T> observer
    ) {
        var registeredObserver = new RegisteredLevelResourceObserver<>(worldName, resourceName, observer);
        registeredResourceObservers.put(worldName, registeredObserver);
        var level = levels.get(worldName);
        if (level != null) {
            level.observeResource(registeredObserver);
        }
        return registeredObserver;
    }

    public <T extends LevelResource> void unobserveResource(RegisteredLevelResourceObserver<T> registeredObserver) {
        var worldName = registeredObserver.getWorldName();
        if (!registeredResourceObservers.remove(worldName, registeredObserver)) {
            return;
        }
        var level = levels.get(worldName);
        if (level != null) {
            level.unobserveResource(registeredObserver);
        }
    }

    public void setLevel(String worldName, String name) {
        var level = new Level(name);
        var oldLevel = levels.put(worldName, level);
        if (oldLevel != null) {
            oldLevel.unobserveAll();
        }
        for (var registeredObserver : registeredResourceObservers.get(worldName)) {
            level.observeResource(registeredObserver);
        }
        loadLevel(level);
    }

    public Level getLevel(String worldName) {
        return levels.get(worldName);
    }

    public void loadLevel(Level level) {
        levelRepository
            .getResourcesByName(level.getName())
            .thenFilterAsync(Objects::nonNull)
            .thenApplyAsync(json -> gson.<Map<String, LevelResource>>fromJson(json, new TypeToken<Map<String, LevelResource>>() {}.getType()))
            .thenAcceptSync(level::addAll);
    }

    public void saveLevel(Level level) {
        Schedulers
            .async()
            .run(() -> {
                var json = gson.toJson(level.getAll());
                levelRepository.setResources(level.getName(), json);
            });
    }
}
