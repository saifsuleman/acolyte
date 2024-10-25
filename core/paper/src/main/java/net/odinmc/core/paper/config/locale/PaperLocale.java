package net.odinmc.core.paper.config.locale;

import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.odinmc.core.common.config.locale.Locale;
import org.bukkit.command.CommandSender;

public class PaperLocale extends Locale<Component, CommandSender> {

    @Override
    protected Component applyReplacements(Component raw, Map<String, Component> replacements) {
        var builder = TextReplacementConfig.builder();
        for (var entry : replacements.entrySet()) {
            var key = entry.getKey();
            var replacement = entry.getValue();
            builder.matchLiteral(key).replacement(replacement);
        }
        return raw.replaceText(builder.build());
    }

    @Override
    protected Component undefined(String name) {
        return Component.text("undefined: " + name, NamedTextColor.RED);
    }

    @Override
    protected void chat(CommandSender receiver, String name, Map<String, Component> replacements) {
        var parsed = this.parse(name, replacements);
        receiver.sendMessage(parsed);
    }
}
