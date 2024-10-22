package net.odinmc.core.common.ref;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.*;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RefMap<K, V> implements Map<K, V> {

    private final Map<K, V> data;
    private final LoadingCache<K, RefEntry> references;

    public RefMap() {
        this.data = new HashMap<>();
        this.references =
            Caffeine
                .newBuilder()
                .weakValues()
                .build(key -> {
                    var present = this.data.containsKey(key);
                    if (!present) {
                        return new RefEntry(key);
                    }
                    return new RefEntry(key, this.data.get(key));
                });
    }

    @Override
    public int size() {
        return this.data.size();
    }

    @Override
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return this.data.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return this.data.containsValue(o);
    }

    @Override
    public V get(Object o) {
        return this.data.get(o);
    }

    @Nullable
    @Override
    public V put(K k, V v) {
        var removed = this.data.put(k, v);
        var ref = this.references.getIfPresent(k);
        if (ref != null) {
            ref.internalSet(v);
        }
        return removed;
    }

    @NotNull
    public Ref<V> ref(K k) {
        return this.references.get(k);
    }

    public Supplier<V> ptr(K k) {
        var ref = this.references.get(k);
        return ref::get;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object o) {
        var removed = this.data.remove(o);
        if (removed == null) {
            return null;
        }
        var key = (K) o;
        var ref = this.references.getIfPresent(key);
        if (ref != null) {
            ref.internalRemove();
            this.references.invalidate(key);
        }
        return removed;
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> map) {
        for (var entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        for (var key : this.data.keySet()) {
            var ref = this.references.getIfPresent(key);
            if (ref != null) {
                ref.internalRemove();
            }
        }

        this.data.clear();
        this.references.invalidateAll();
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(this.data.keySet());
    }

    @NotNull
    @Override
    public Collection<V> values() {
        return Collections.unmodifiableCollection(this.data.values());
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return Collections.unmodifiableSet(this.data.entrySet());
    }

    public class RefEntry extends Ref<V> {

        private final K key;

        public RefEntry(K key, V value) {
            super(value);
            this.key = key;
        }

        public RefEntry(K key) {
            this.key = key;
        }

        @Override
        public V set(V value) {
            return RefMap.this.put(this.key, value);
        }

        @Override
        public V remove() {
            return RefMap.this.remove(this.key);
        }

        private void internalSet(V value) {
            super.set(value);
        }

        private void internalRemove() {
            super.remove();
        }
    }
}
