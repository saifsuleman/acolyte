package net.odinmc.core.common.services;

import com.google.common.reflect.TypeToken;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import net.odinmc.core.common.ref.Ref;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public class Services {

    private static final AtomicReference<ServiceProvider> PROVIDER = new AtomicReference<>(new RefServiceProvider());

    public static void changeProvider(@NotNull final ServiceProvider provider) {
        Services.PROVIDER.set(provider);
    }

    public static <S extends A, A> S provideApi(final S instance, final Class<A> apiClass) {
        Services.provide(apiClass, instance);
        Services.provide((Class<S>) instance.getClass(), instance);
        return instance;
    }

    @NotNull
    public static <T> Optional<T> get(@NotNull final Class<T> serviceClass) {
        return Services.get(TypeToken.of(serviceClass));
    }

    @NotNull
    public static <T> Optional<T> get(@NotNull final TypeToken<T> serviceType) {
        return Services.PROVIDER.get().get(serviceType);
    }

    public static <T> Optional<T> remove(final Class<T> serviceClass) {
        return Services.remove(TypeToken.of(serviceClass));
    }

    public static <T> Optional<T> remove(final TypeToken<T> serviceType) {
        return Services.PROVIDER.get().remove(serviceType);
    }

    public static <T> Ref<T> ref(TypeToken<T> serviceType) {
        return Services.PROVIDER.get().ref(serviceType);
    }

    public static <T> Ref<T> refBound(TypeToken<T> serviceType, Object bound) {
        return Services.PROVIDER.get().refBound(serviceType, bound);
    }

    public static <T> Ref<T> refBound(Class<T> serviceClass, Object bound) {
        return Services.refBound(TypeToken.of(serviceClass), bound);
    }

    public static <T> Ref<T> ref(Class<T> serviceClass) {
        return Services.ref(TypeToken.of(serviceClass));
    }

    @NotNull
    public static <T> Optional<T> getBound(@NotNull final Class<T> serviceClass, @NotNull final Object bound) {
        return Services.getBound(TypeToken.of(serviceClass), bound);
    }

    @NotNull
    public static <T> Optional<T> getBound(@NotNull final TypeToken<T> serviceType, @NotNull final Object bound) {
        return Services.PROVIDER.get().getBound(serviceType, bound);
    }

    @NotNull
    public static <T> T getOrProvide(@NotNull final Class<T> serviceClass) {
        return Services.getOrProvide(TypeToken.of(serviceClass));
    }

    @NotNull
    public static <T> T getOrProvide(@NotNull final TypeToken<T> serviceType) {
        return Services.get(serviceType).orElseGet(() -> Services.provide(serviceType, Services.initiate(serviceType)));
    }

    @NotNull
    public static <T> T getOrProvideBound(@NotNull final Class<T> serviceClass, @NotNull final Object bound) {
        return Services.getOrProvideBound(TypeToken.of(serviceClass), bound);
    }

    @NotNull
    public static <T> T getOrProvideBound(@NotNull final TypeToken<T> serviceType, @NotNull final Object bound) {
        return Services.getBound(serviceType, bound).orElseGet(() -> Services.provideBound(serviceType, Services.initiate(serviceType), bound));
    }

    @NotNull
    public static <T> T load(@NotNull final Class<T> serviceClass) {
        return Services.load(TypeToken.of(serviceClass));
    }

    @NotNull
    public static <T> T load(@NotNull final TypeToken<T> serviceType) {
        return Services
            .get(serviceType)
            .orElseThrow(() -> new IllegalStateException("No registration present for service '%s'".formatted(serviceType.getRawType().getName())));
    }

    @NotNull
    public static <T> T loadBound(@NotNull final Class<T> serviceClass, @NotNull final Object bound) {
        return Services.loadBound(TypeToken.of(serviceClass), bound);
    }

    @NotNull
    public static <T> T loadBound(@NotNull final TypeToken<T> serviceType, @NotNull final Object bound) {
        return Services
            .getBound(serviceType, bound)
            .orElseThrow(() -> new IllegalStateException("No registration present for service '%s'".formatted(serviceType.getRawType().getName())));
    }

    @NotNull
    public static <T, I extends T> I provide(@NotNull final Class<T> serviceClass, @NotNull final I instance) {
        return Services.provide(TypeToken.of(serviceClass), instance);
    }

    @NotNull
    public static <T, I extends T> I provide(@NotNull final TypeToken<T> serviceType, @NotNull final I instance) {
        return Services.PROVIDER.get().provide(serviceType, instance);
    }

    @NotNull
    public static <T, I extends T> I provideBound(@NotNull final Class<T> serviceClass, @NotNull final I instance, @NotNull final Object bound) {
        return Services.provideBound(TypeToken.of(serviceClass), instance, bound);
    }

    @NotNull
    public static <T, I extends T> I provideBound(@NotNull final TypeToken<T> serviceType, @NotNull final I instance, @NotNull final Object bound) {
        return Services.PROVIDER.get().provideBound(serviceType, instance, bound);
    }

    @NotNull
    private static <T> T initiate(@NotNull final TypeToken<T> type) {
        try {
            return (T) type.getRawType().getConstructor().newInstance();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
