package net.odinmc.core.paper.cloud;

import com.google.common.reflect.TypeToken;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginBootstrapContextImpl;
import io.papermc.paper.plugin.configuration.PluginMeta;
import lombok.SneakyThrows;
import net.odinmc.core.common.services.Services;
import net.odinmc.core.common.terminable.Terminable;
import net.odinmc.core.common.util.ReflectionUtil;
import net.odinmc.core.paper.plugin.ExtendedJavaPlugin;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.CommandMeta;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.setting.ManagerSetting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Cloud implements Terminable {
    private final PaperCommandManager<CommandSender> commandManager;
    private final AnnotationParser<CommandSender> annotationParser;
    private final CloudBootstrapHack hack;

    private Cloud(PaperCommandManager<CommandSender> commandManager, AnnotationParser<CommandSender> annotationParser, CloudBootstrapHack hack) {
        this.commandManager = commandManager;
        this.annotationParser = annotationParser;
        this.hack = hack;
    }

    public void register(Object...instances) {
        annotationParser.parse(instances);
        hack.fire();
    }

    public void register() {
        hack.fire();
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static Cloud create(
        @NotNull final ExecutionCoordinator<CommandSender> coordinator
    ) {
        final var senderMapper = new CommandSenderMapper();
        final var hack = new CloudBootstrapHack();
        final var manager = PaperCommandManager.builder(senderMapper).executionCoordinator(coordinator).buildBootstrapped(hack.getContext());
        manager.settings().set(ManagerSetting.ALLOW_UNSAFE_REGISTRATION, true);
        manager.settings().set(ManagerSetting.OVERRIDE_EXISTING_COMMANDS, true);
        var parser = new AnnotationParser<>(manager, CommandSender.class, p -> CommandMeta.empty());
        return new Cloud(manager, parser, hack);
    }

    public void registerBaseHelpCommand(
        @NotNull final PaperCommandManager<CommandSender> commandManager,
        @NotNull final Command.Builder<CommandSender> builder,
        @NotNull final String command
    ) {
        registerBaseHelpCommand(builder, command, null);
    }

    public void registerBaseHelpCommand(
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

    public void registerHelpCommand(
        @NotNull final Command.Builder<CommandSender> builder,
        @NotNull final String command
    ) {
        registerHelpCommand(builder, command, null);
    }

    public void registerHelpCommand(
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

    @Override
    public void close() {
        commandManager.rootCommands().forEach(commandManager::deleteRootCommand);
    }
}
