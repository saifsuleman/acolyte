package net.odinmc.core.common.delegates;

import java.util.function.Consumer;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

public record ConsumerToPredicate<T>(@NotNull Consumer<T> delegate, boolean fallback) implements Predicate<T> {
    @Override
    public boolean test(final T t) {
        this.delegate.accept(t);
        return this.fallback;
    }
}
