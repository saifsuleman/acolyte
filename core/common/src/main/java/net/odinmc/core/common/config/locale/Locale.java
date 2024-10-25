package net.odinmc.core.common.config.locale;

import java.util.HashMap;
import java.util.Map;

public abstract class Locale<T, R> {

    private final Map<String, T> globalConfig = new HashMap<>();
    private final Map<String, T> serverConfig = new HashMap<>();

    protected abstract T applyReplacements(T raw, Map<String, T> replacements);

    protected abstract T undefined(String name);

    protected abstract void chat(R receiver, String name, Map<String, T> replacements);

    public void chat(R receiver, String name) {
        chat(receiver, name, null);
    }

    public T parse(String name) {
        return parse(name, null);
    }

    public ScopedLocale<T, R> scope(String qualifier) {
        return new ScopedLocale<>(qualifier, this);
    }

    public T parse(String name, Map<String, T> replacements) {
        T message = serverConfig.get(name);
        if (message == null) {
            message = globalConfig.get(name);
        }
        if (message == null) {
            message = undefined(name);
        }

        if (replacements == null || replacements.isEmpty()) {
            return message;
        }

        return applyReplacements(message, replacements);
    }

    public Map<String, T> getGlobalConfig() {
        return globalConfig;
    }

    public Map<String, T> getServerConfig() {
        return serverConfig;
    }
}
