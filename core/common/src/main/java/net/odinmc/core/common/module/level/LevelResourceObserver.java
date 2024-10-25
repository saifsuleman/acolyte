package net.odinmc.core.common.module.level;

public interface LevelResourceObserver<T extends LevelResource> {
    void onObserve(String name, T resource);

    void onUnobserve(String name, T resource);
}
