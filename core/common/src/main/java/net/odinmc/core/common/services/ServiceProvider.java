package net.odinmc.core.common.services;

import com.google.common.reflect.TypeToken;
import java.util.Optional;
import net.odinmc.core.common.ref.Ref;
import org.jetbrains.annotations.NotNull;

public interface ServiceProvider {
    @NotNull
    <T> Optional<T> get(@NotNull TypeToken<T> serviceType);

    @NotNull
    <T> Optional<T> getBound(@NotNull TypeToken<T> serviceType, @NotNull Object bound);

    <T> Ref<T> ref(@NotNull TypeToken<T> serviceType);

    <T> Ref<T> refBound(@NotNull TypeToken<T> serviceType, @NotNull Object bound);

    @NotNull
    <T, I extends T> I provide(@NotNull TypeToken<T> serviceType, @NotNull I instance);

    @NotNull
    <T, I extends T> I provideBound(@NotNull TypeToken<T> serviceType, @NotNull I instance, @NotNull Object bound);
}
