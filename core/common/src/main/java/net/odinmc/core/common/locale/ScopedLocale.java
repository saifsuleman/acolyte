package net.odinmc.core.common.locale;

import java.util.Map;

public class ScopedLocale<T, R> extends Locale<T, R> {
    private final String qualifier;
    private final Locale<T, R> delegate;

    public ScopedLocale(String qualifier, Locale<T, R> delegate) {
        this.qualifier = qualifier;
        this.delegate = delegate;
    }

    @Override
    public T parse(String name, Map<String, T> replacements) {
        if (name.startsWith("@")) {
            name = qualifier + "." + name.substring(1);
        }

        return super.parse(name, replacements);
    }

    @Override
    protected T applyReplacements(T raw, Map<String, T> replacements) {
        return delegate.applyReplacements(raw, replacements);
    }

    @Override
    protected T undefined() {
        return delegate.undefined();
    }

    @Override
    protected void chat(R receiver, String name, Map<String, T> replacements) {
        delegate.chat(receiver, name, replacements);
    }
}
