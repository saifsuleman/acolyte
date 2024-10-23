package net.odinmc.core.common.module.redirect;

import java.util.UUID;

public record RedirectKickMessage(UUID uuid, String kickMessage, int loop) {
    public static String REDIRECT_PREFIX = "redirect:";

    public boolean isRedirect() {
        return kickMessage.startsWith(REDIRECT_PREFIX);
    }

    public String getRedirectServer() {
        return kickMessage.substring(REDIRECT_PREFIX.length());
    }
}
