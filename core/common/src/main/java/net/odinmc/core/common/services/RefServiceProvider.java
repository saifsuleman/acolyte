package net.odinmc.core.common.services;

import com.google.common.reflect.TypeToken;
import java.util.Optional;
import net.odinmc.core.common.ref.Ref;
import net.odinmc.core.common.ref.RefMap;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
final class RefServiceProvider implements ServiceProvider {

    private final RefMap<Object, RefMap<TypeToken<?>, Object>> boundServices = new RefMap<>();
    private final RefMap<TypeToken<?>, Object> services = new RefMap<>();

    @NotNull
    @Override
    public <T> Optional<T> get(@NotNull final TypeToken<T> serviceType) {
        return Optional.ofNullable((T) this.services.get(serviceType));
    }

    @NotNull
    @Override
    public <T> Optional<T> getBound(@NotNull final TypeToken<T> serviceType, @NotNull final Object bound) {
        return Optional.ofNullable(this.boundServices.get(bound)).map(map -> (T) map.get(serviceType));
    }

    @Override
    public <T> Ref<T> ref(@NotNull TypeToken<T> serviceType) {
        return (Ref<T>) this.services.ref(serviceType);
    }

    @Override
    public <T> Ref<T> refBound(@NotNull TypeToken<T> serviceType, @NotNull Object bound) {
        final var map = this.boundServices.computeIfAbsent(bound, o -> new RefMap<>());
        return (Ref<T>) map.ref(serviceType);
    }

    @NotNull
    @Override
    public <T, I extends T> I provide(@NotNull final TypeToken<T> serviceType, @NotNull final I instance) {
        this.services.put(serviceType, instance);
        return instance;
    }

    @NotNull
    @Override
    public <T, I extends T> I provideBound(@NotNull final TypeToken<T> serviceType, @NotNull final I instance, @NotNull final Object bound) {
        final var map = this.boundServices.computeIfAbsent(bound, o -> new RefMap<>());
        map.put(serviceType, instance);
        return instance;
    }
}
