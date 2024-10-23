package net.odinmc.core.paper.util;

import io.papermc.paper.adventure.PaperAdventure;
import java.util.Collection;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.Packet;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketUtil {

    public static void sendPacket(Collection<Player> players, Packet<?> packet) {
        players.forEach(player -> sendPacket(player, packet));
    }

    public static void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    public static void writeComponent(FriendlyByteBuf buf, Component component) {
        var nmsComponent = PaperAdventure.asVanilla(component);
        ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.encode(buf, nmsComponent);
    }

    public static void writeComponentOptional(RegistryFriendlyByteBuf buf, Component component) {
        if (component == null) {
            ComponentSerialization.TRUSTED_OPTIONAL_STREAM_CODEC.encode(buf, Optional.empty());
            return;
        }

        var nmsComponent = PaperAdventure.asVanilla(component);
        ComponentSerialization.TRUSTED_OPTIONAL_STREAM_CODEC.encode(buf, Optional.of(nmsComponent));
    }
}
