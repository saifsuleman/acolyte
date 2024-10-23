package net.odinmc.essentials.module.commands;

import net.odinmc.core.common.terminable.TerminableConsumer;
import net.odinmc.core.common.terminable.module.TerminableModule;
import net.odinmc.core.paper.cloud.Cloud;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.execution.ExecutionCoordinator;

public class PlayerCommandsModule implements TerminableModule {
    @Override
    public void setup(TerminableConsumer consumer) {
        var cloud = Cloud.create(ExecutionCoordinator.asyncCoordinator());
        cloud.bindWith(consumer);
        cloud.register(this);
    }

    @Command("odin test <message>")
    public void playerTestCommand(Player sender, @Greedy @Argument("message") String message) {
    }
}
