package net.odinmc.core.common.module.redirect;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PlayerRedirectCache {

    private static final RedirectKickCallback noop = value -> {};

    private final Cache<String, Entry> redirects = Caffeine.newBuilder().expireAfterWrite(20, TimeUnit.SECONDS).build();

    public boolean tryRedirect(String server, RedirectKickCallback callback, Map<String, Object> additionalData) {
        if (callback == null) {
            callback = noop;
        }
        return redirects.asMap().putIfAbsent(server, new Entry(callback, additionalData)) == null;
    }

    public Entry removeRedirect(String server) {
        return redirects.asMap().remove(server);
    }

    public Entry getRedirect(String server) {
        return redirects.getIfPresent(server);
    }

    public record Entry(RedirectKickCallback callback, Map<String, Object> additionalData) {}
}
