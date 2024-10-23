package net.odinmc.core.paper.locale;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.odinmc.core.common.locale.Locale;
import org.bukkit.command.CommandSender;

import java.util.Map;

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
    protected Component undefined() {
        return Component.text("undefined", NamedTextColor.RED);
    }

    @Override
    protected void chat(CommandSender receiver, String name, Map<String, Component> replacements) {
        var parsed = this.parse(name, replacements);
        receiver.sendMessage(parsed);
    }
}
