package net.odinmc.core.common.module.level;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class Level extends LevelResourceSet<LevelResource> {

    private final String name;
    private final Multimap<String, RegisteredLevelResourceObserver> registeredResourceObservers = HashMultimap.create();

    public Level(String name) {
        this.name = name;
    }

    protected <T extends LevelResource> void observeResource(RegisteredLevelResourceObserver<T> registeredObserver) {
        var resourceName = registeredObserver.getResourceName();
        registeredResourceObservers.put(resourceName, registeredObserver);
        var resource = get(resourceName);
        if (resource != null) {
            registeredObserver.observe((T) resource);
        }
    }

    protected <T extends LevelResource> void unobserveResource(RegisteredLevelResourceObserver<T> registeredObserver) {
        registeredResourceObservers.remove(registeredObserver.getResourceName(), registeredObserver);
        if (registeredObserver.isObserving()) {
            registeredObserver.unobserve();
        }
    }

    protected void unobserveAll() {
        for (var resourceName : getAll().keySet()) {
            onUnobserveResource(resourceName);
        }
        registeredResourceObservers.clear();
    }

    @Override
    protected void onResourceAdd(String resourceName, LevelResource resource) {
        if (resource instanceof LevelResourceSet) {
            LevelResourceSet<?> resourceSet = (LevelResourceSet<?>) resource;
            resourceSet.setParentLevel(this);
            resourceSet.setName(resourceName);
        }
        onObserveResource(resourceName, resource);
    }

    @Override
    protected void onResourceRemove(String resourceName, LevelResource resource) {
        if (resource instanceof LevelResourceSet) {
            LevelResourceSet<?> resourceSet = (LevelResourceSet<?>) resource;
            resourceSet.setParentLevel(null);
            resourceSet.setName(null);
        }
        onUnobserveResource(resourceName);
    }

    protected void onResourceModify(String resourceName, LevelResource resource) {
        onObserveResource(resourceName, resource);
    }

    private void onObserveResource(String resourceName, LevelResource resource) {
        for (var resourceObserver : registeredResourceObservers.get(resourceName)) {
            try {
                resourceObserver.observe(resource);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private void onUnobserveResource(String resourceName) {
        for (var resourceObserver : registeredResourceObservers.get(resourceName)) {
            try {
                resourceObserver.unobserve();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public String getName() {
        return name;
    }
}
