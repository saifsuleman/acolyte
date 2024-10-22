package net.odinmc.core.common.definitions;

import com.google.common.reflect.TypeToken;

public interface TypeAware<T> {
    TypeToken<T> getType();
}
