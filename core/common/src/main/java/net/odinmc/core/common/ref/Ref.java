package net.odinmc.core.common.ref;

public class Ref<T> {

    private volatile T value;
    private volatile boolean present;

    public Ref() {
        this.present = false;
    }

    public Ref(T value) {
        this.value = value;
        this.present = true;
    }

    public synchronized T get() {
        return this.value;
    }

    public synchronized T set(T value) {
        var original = this.value;
        this.value = value;
        this.present = true;
        return original;
    }

    public synchronized T remove() {
        var original = this.value;
        this.value = null;
        this.present = false;
        return original;
    }

    public synchronized boolean isPresent() {
        return present;
    }
}
