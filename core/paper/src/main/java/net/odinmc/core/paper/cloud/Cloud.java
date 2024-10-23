package net.odinmc.core.paper.cloud;

import com.google.common.reflect.TypeToken;
import lombok.SneakyThrows;
import net.odinmc.core.common.services.Services;
import net.odinmc.core.paper.plugin.ExtendedJavaPlugin;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.CommandMeta;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.setting.ManagerSetting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Cloud {
    TypeToken<PaperCommandManager<CommandSender>> TYPE = new TypeToken<>() {};
    TypeToken<AnnotationParser<CommandSender>> ANNOTATION_PARSER = new TypeToken<>() {};

    static PaperCommandManager<CommandSender> get(Class<? extends ExtendedJavaPlugin> pluginClass) {
        return Services.loadBound(Cloud.TYPE, pluginClass);
    }

    static AnnotationParser<CommandSender> getAnnotationParser(Class<? extends ExtendedJavaPlugin> pluginClass) {
        return Services.loadBound(Cloud.ANNOTATION_PARSER, pluginClass);
    }

    @NotNull
    static AnnotationParser<CommandSender> createAnnotationParser(@NotNull final Class<? extends ExtendedJavaPlugin> plugin) {
        final var commandManager = Cloud.get(plugin);
        return new AnnotationParser<>(commandManager, CommandSender.class, p -> CommandMeta.empty());
    }

    @SneakyThrows
    static PaperCommandManager<CommandSender> create(
        @NotNull final ExtendedJavaPlugin plugin,
        @NotNull final ExecutionCoordinator<CommandSender> coordinator
    ) {
        final var manager = PaperCommandManager.builder(new CommandSenderMapper()).executionCoordinator(coordinator).buildOnEnable(plugin);
        manager.settings().set(ManagerSetting.ALLOW_UNSAFE_REGISTRATION, true);
        manager.settings().set(ManagerSetting.OVERRIDE_EXISTING_COMMANDS, true);
        return manager;
    }

    static void registerBaseHelpCommand(
        @NotNull final PaperCommandManager<CommandSender> commandManager,
        @NotNull final Command.Builder<CommandSender> builder,
        @NotNull final String command
    ) {
        Cloud.registerBaseHelpCommand(commandManager, builder, command, null);
    }

    static void registerBaseHelpCommand(
        @NotNull final PaperCommandManager<CommandSender> commandManager,
        @NotNull final Command.Builder<CommandSender> builder,
        @NotNull final String command,
        @Nullable final String permission
    ) {
        final var helpHandler = MinecraftHelp.createNative("/" + command, commandManager);
        final var help = builder
            .optional("query", StringParser.greedyStringParser())
            .handler(context -> helpHandler.queryCommands(context.getOrDefault("query", ""), context.sender()));
        commandManager.command(permission == null ? help : help.permission(permission));
    }

    static void registerHelpCommand(
        @NotNull final PaperCommandManager<CommandSender> commandManager,
        @NotNull final Command.Builder<CommandSender> builder,
        @NotNull final String command
    ) {
        Cloud.registerHelpCommand(commandManager, builder, command, null);
    }

    static void registerHelpCommand(
        @NotNull final PaperCommandManager<CommandSender> commandManager,
        @NotNull final Command.Builder<CommandSender> builder,
        @NotNull final String command,
        @Nullable final String permission
    ) {
        final var helpHandler = MinecraftHelp.createNative("/" + command + " help", commandManager);
        final var help = builder
            .literal("help", "?")
            .optional("query", StringParser.greedyStringParser())
            .handler(context -> helpHandler.queryCommands(context.getOrDefault("query", ""), context.sender()));
        commandManager.command(permission == null ? help : help.permission(permission));
    }
}
