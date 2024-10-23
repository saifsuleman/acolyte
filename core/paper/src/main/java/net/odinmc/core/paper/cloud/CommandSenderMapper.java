package net.odinmc.core.paper.cloud;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.SenderMapper;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("ALL")
public class CommandSenderMapper implements SenderMapper<CommandSourceStack, CommandSender> {

    @Override
    public @NonNull CommandSender map(@NonNull CommandSourceStack base) {
        return base.getSender();
    }

    @Override
    public @NonNull CommandSourceStack reverse(@NonNull CommandSender mapped) {
        return new CommandSourceStack() {
            @Override
            public Location getLocation() {
                if (!(mapped instanceof Player player)) {
                    return null;
                }

                return player.getLocation();
            }

            @Override
            public CommandSender getSender() {
                return mapped;
            }

            @Override
            public @Nullable Entity getExecutor() {
                if (!(mapped instanceof Entity entity)) {
                    return null;
                }

                return entity;
            }
        };
    }
}
