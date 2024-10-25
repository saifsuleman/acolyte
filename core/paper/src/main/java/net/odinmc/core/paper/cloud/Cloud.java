package net.odinmc.core.paper.cloud;

import lombok.SneakyThrows;
import net.odinmc.core.common.terminable.Terminable;
import net.odinmc.core.common.terminable.TerminableConsumer;
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

public class Cloud implements Terminable {

    private final PaperCommandManager<CommandSender> commandManager;
    private final AnnotationParser<CommandSender> annotationParser;
    private final CloudBootstrapHack hack;

    private Cloud(PaperCommandManager<CommandSender> commandManager, AnnotationParser<CommandSender> annotationParser, CloudBootstrapHack hack) {
        this.commandManager = commandManager;
        this.annotationParser = annotationParser;
        this.hack = hack;
    }

    public static Cloud create(@NotNull final TerminableConsumer consumer) {
        return Cloud.create(consumer, ExecutionCoordinator.asyncCoordinator());
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static Cloud create(@NotNull final TerminableConsumer consumer, @NotNull final ExecutionCoordinator<CommandSender> coordinator) {
        final var senderMapper = new CommandSenderMapper();
        final var hack = new CloudBootstrapHack();
        final var manager = PaperCommandManager.builder(senderMapper).executionCoordinator(coordinator).buildBootstrapped(hack.getContext());
        manager.settings().set(ManagerSetting.ALLOW_UNSAFE_REGISTRATION, true);
        manager.settings().set(ManagerSetting.OVERRIDE_EXISTING_COMMANDS, true);
        var parser = new AnnotationParser<>(manager, CommandSender.class, p -> CommandMeta.empty());
        var cloud = new Cloud(manager, parser, hack);
        cloud.bindWith(consumer);
        return cloud;
    }

    public PaperCommandManager<CommandSender> commandManager() {
        return commandManager;
    }

    public AnnotationParser<CommandSender> annotationParser() {
        return annotationParser;
    }

    public void register(Object... instances) {
        annotationParser.parse(instances);
        hack.fire();
    }

    public void register() {
        hack.fire();
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

    public void registerHelpCommand(@NotNull final Command.Builder<CommandSender> builder, @NotNull final String command) {
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
