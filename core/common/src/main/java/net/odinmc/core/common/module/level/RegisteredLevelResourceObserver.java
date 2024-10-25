package net.odinmc.core.common.module.level;

public class RegisteredLevelResourceObserver<T extends LevelResource> {

    private final String worldName;
    private final String resourceName;
    private final LevelResourceObserver<T> observer;
    private T observed;

    public RegisteredLevelResourceObserver(String worldName, String resourceName, LevelResourceObserver<T> observer) {
        this.worldName = worldName;
        this.resourceName = resourceName;
        this.observer = observer;
    }

    protected void observe(T resource) {
        // Duplicate observe calls are allowed for ResourceSet changes
        if (observed != null && observed != resource) {
            throw new IllegalStateException("Resource already observed");
        }
        observer.onObserve(resourceName, resource);
        observed = resource;
    }

    protected void unobserve() {
        if (observed == null) {
            throw new IllegalStateException("Resource not observed");
        }
        observer.onUnobserve(resourceName, observed);
        observed = null;
    }

    protected boolean isObserving() {
        return observed != null;
    }

    public String getWorldName() {
        return worldName;
    }

    public String getResourceName() {
        return resourceName;
    }
}
