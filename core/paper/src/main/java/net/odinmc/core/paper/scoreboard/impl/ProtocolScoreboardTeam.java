package net.odinmc.core.paper.scoreboard.impl;

import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.server.MinecraftServer;
import net.odinmc.core.common.util.ConcurrentHashSet;
import net.odinmc.core.paper.scoreboard.AbstractPaperScoreboardTeam;
import net.odinmc.core.paper.util.PacketUtil;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class ProtocolScoreboardTeam extends AbstractPaperScoreboardTeam {

    private static final int TEAM_MODE_CREATE = 0;
    private static final int TEAM_MODE_REMOVE = 1;
    private static final int TEAM_MODE_UPDATE = 2;
    private static final int TEAM_MODE_ADD_PLAYERS = 3;
    private static final int TEAM_MODE_REMOVE_PLAYERS = 4;

    private static final ImmutableMap<Team.OptionStatus, String> NAME_TAG_VISIBILITY_STRINGS = ImmutableMap.of(
        Team.OptionStatus.ALWAYS,
        "always",
        Team.OptionStatus.NEVER,
        "never",
        Team.OptionStatus.FOR_OWN_TEAM,
        "hideForOtherTeams",
        Team.OptionStatus.FOR_OTHER_TEAMS,
        "hideForOwnTeam"
    );

    private final Set<Player> subscribers = new ConcurrentHashSet<>();

    public ProtocolScoreboardTeam(ProtocolScoreboard scoreboard, String name, Component title) {
        super(scoreboard, name, title);
    }

    @Override
    protected void subscribe(Player player) {
        if (!subscribers.add(player)) {
            return;
        }
        PacketUtil.sendPacket(subscribers, newCreatePacket());
    }

    @Override
    protected void unsubscribe(Player player, boolean disconnected) {
        if (!subscribers.remove(player)) {
            return;
        }
        if (disconnected) {
            return;
        }
        PacketUtil.sendPacket(subscribers, newPacket(TEAM_MODE_REMOVE));
    }

    @Override
    protected void unsubscribeAll() {
        PacketUtil.sendPacket(subscribers, newPacket(TEAM_MODE_REMOVE));
        subscribers.clear();
    }

    @Override
    protected void updateTitle(Component title) {
        PacketUtil.sendPacket(subscribers, newUpdatePacket());
    }

    @Override
    protected void updatePrefix(Component prefix) {
        PacketUtil.sendPacket(subscribers, newUpdatePacket());
    }

    @Override
    protected void updateSuffix(Component suffix) {
        PacketUtil.sendPacket(subscribers, newUpdatePacket());
    }

    @Override
    protected void updateFriendlyFire(boolean friendlyFire) {
        PacketUtil.sendPacket(subscribers, newUpdatePacket());
    }

    @Override
    protected void updateFriendlyInvisibles(boolean friendlyInvisibles) {
        PacketUtil.sendPacket(subscribers, newUpdatePacket());
    }

    @Override
    protected void updateNameTagVisibility(Team.OptionStatus nameTagVisibility) {
        PacketUtil.sendPacket(subscribers, newUpdatePacket());
    }

    @Override
    protected void updateColor(TextColor color) {
        PacketUtil.sendPacket(subscribers, newUpdatePacket());
    }

    @Override
    protected void updateEntryAdd(String entry) {
        PacketUtil.sendPacket(subscribers, newPacket(TEAM_MODE_ADD_PLAYERS, Collections.singletonList(entry)));
    }

    @Override
    protected void updateEntryRemove(String entry) {
        PacketUtil.sendPacket(subscribers, newPacket(TEAM_MODE_REMOVE_PLAYERS, Collections.singletonList(entry)));
    }

    private ClientboundSetPlayerTeamPacket newCreatePacket() {
        return newPacket(TEAM_MODE_CREATE, this.getEntries());
    }

    private ClientboundSetPlayerTeamPacket newRemovePacket() {
        return newPacket(TEAM_MODE_REMOVE);
    }

    private ClientboundSetPlayerTeamPacket newUpdatePacket() {
        return newPacket(TEAM_MODE_UPDATE);
    }

    private <T> T pickNonNull(T a, T b) {
        if (a != null) {
            return a;
        }

        if (b != null) {
            return b;
        }

        throw new IllegalArgumentException("a and b both null");
    }

    private ClientboundSetPlayerTeamPacket newPacket(int mode) {
        return newPacket(mode, Collections.emptyList());
    }

    private ClientboundSetPlayerTeamPacket newPacket(int mode, Collection<String> entries) {
        var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), MinecraftServer.getServer().registryAccess());

        buf.writeUtf(this.getName());
        buf.writeByte(mode);

        if (mode == TEAM_MODE_CREATE || mode == TEAM_MODE_UPDATE) {
            var flag = 0;
            if (this.isFriendlyFire()) {
                flag |= 1;
            }
            if (this.isFriendlyInvisibles()) {
                flag |= 2;
            }

            PacketUtil.writeComponent(buf, getTitle());
            buf.writeByte(flag);

            buf.writeUtf(pickNonNull(NAME_TAG_VISIBILITY_STRINGS.get(getNameTagVisibility()), "always"));
            buf.writeUtf("always");
            buf.writeEnum(pickNonNull(ChatFormatting.getByHexValue(getColor().value()), ChatFormatting.WHITE));
            PacketUtil.writeComponent(buf, getPrefix());
            PacketUtil.writeComponent(buf, getSuffix());
        }

        if (mode == TEAM_MODE_CREATE || mode == TEAM_MODE_ADD_PLAYERS || mode == TEAM_MODE_REMOVE_PLAYERS) {
            buf.writeCollection(entries, FriendlyByteBuf::writeUtf);
        }

        return ClientboundSetPlayerTeamPacket.STREAM_CODEC.decode(buf);
    }
}
