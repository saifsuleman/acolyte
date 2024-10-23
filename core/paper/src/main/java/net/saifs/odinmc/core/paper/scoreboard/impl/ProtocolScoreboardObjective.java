package net.saifs.odinmc.core.paper.scoreboard.impl;

import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.numbers.BlankFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import net.minecraft.network.protocol.game.ClientboundResetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.odinmc.core.common.util.ConcurrentHashSet;
import net.saifs.odinmc.core.paper.scoreboard.AbstractPaperScoreboardObjective;
import net.saifs.odinmc.core.paper.scoreboard.NumberFormat;
import net.saifs.odinmc.core.paper.util.PacketUtil;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public class ProtocolScoreboardObjective extends AbstractPaperScoreboardObjective {

    private static final int OBJECTIVE_MODE_CREATE = 0;
    private static final int OBJECTIVE_MODE_REMOVE = 1;
    private static final int OBJECTIVE_MODE_UPDATE = 2;

    private static final ImmutableMap<DisplaySlot, Integer> SLOT_POSITIONS = ImmutableMap.of(
        DisplaySlot.PLAYER_LIST,
        0,
        DisplaySlot.SIDEBAR,
        1,
        DisplaySlot.BELOW_NAME,
        2
    );

    private final ProtocolScoreboard scoreboard;
    private final ObjectiveCriteria.RenderType healthDisplay;
    private final Set<Player> subscribers = new ConcurrentHashSet<>();

    public ProtocolScoreboardObjective(
        ProtocolScoreboard scoreboard,
        String name,
        Component title,
        String criteria,
        DisplaySlot slot,
        NumberFormat styledFormat
    ) {
        super(scoreboard, name, title, slot, styledFormat);
        this.scoreboard = scoreboard;
        this.healthDisplay = criteria.equalsIgnoreCase("health") ? ObjectiveCriteria.RenderType.HEARTS : ObjectiveCriteria.RenderType.INTEGER;
    }

    @Override
    protected void subscribe(Player player) {
        if (!subscribers.add(player)) {
            return;
        }
        PacketUtil.sendPacket(player, newObjectivePacket(OBJECTIVE_MODE_CREATE));
        PacketUtil.sendPacket(player, newDisplaySlotPacket());
        for (var score : getScores().entrySet()) {
            PacketUtil.sendPacket(player, newScorePacket(score.getKey(), score.getValue()));
        }
    }

    @Override
    protected void unsubscribe(Player player, boolean disconnected) {
        if (!subscribers.remove(player)) {
            return;
        }
        if (disconnected) {
            return;
        }
        PacketUtil.sendPacket(player, newObjectivePacket(OBJECTIVE_MODE_REMOVE));
    }

    @Override
    protected void unsubscribeAll() {
        PacketUtil.sendPacket(subscribers, newObjectivePacket(OBJECTIVE_MODE_REMOVE));
        subscribers.clear();
    }

    @Override
    protected void updateTitle(Component title) {
        PacketUtil.sendPacket(subscribers, newObjectivePacket(OBJECTIVE_MODE_UPDATE));
    }

    @Override
    protected void updateSlot(DisplaySlot slot) {
        PacketUtil.sendPacket(subscribers, newDisplaySlotPacket());
    }

    @Override
    protected void updateScore(String name, ScoreValue value) {
        PacketUtil.sendPacket(subscribers, newScorePacket(name, value));
    }

    @Override
    protected void updateRemove(String name) {
        PacketUtil.sendPacket(subscribers, newResetScorePacket(name));
    }

    @Override
    protected void updateClear() {
        PacketUtil.sendPacket(subscribers, newObjectivePacket(OBJECTIVE_MODE_REMOVE));
        PacketUtil.sendPacket(subscribers, newObjectivePacket(OBJECTIVE_MODE_CREATE));
        PacketUtil.sendPacket(subscribers, newDisplaySlotPacket());
    }

    @Override
    protected void updateStyledFormat(NumberFormat styledFormat) {
        PacketUtil.sendPacket(subscribers, newObjectivePacket(OBJECTIVE_MODE_UPDATE));
    }

    private ClientboundSetObjectivePacket newObjectivePacket(int mode) {
        var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), MinecraftServer.getServer().registryAccess());
        buf.writeUtf(this.getName());
        buf.writeByte(mode);
        if (mode == OBJECTIVE_MODE_CREATE || mode == OBJECTIVE_MODE_UPDATE) {
            PacketUtil.writeComponent(buf, getTitle());
            buf.writeEnum(this.healthDisplay);
            encodeStyledFormat(buf, getStyledFormat());
        }
        return ClientboundSetObjectivePacket.STREAM_CODEC.decode(buf);
    }

    private void encodeStyledFormat(RegistryFriendlyByteBuf buf, NumberFormat styledFormat) {
        if (styledFormat == null) {
            styledFormat = NumberFormat.BLANK;
        }

        switch (styledFormat) {
            case BLANK -> {
                NumberFormatTypes.OPTIONAL_STREAM_CODEC.encode(buf, Optional.of(BlankFormat.INSTANCE));
            }
            case NO_STYLE -> {
                NumberFormatTypes.OPTIONAL_STREAM_CODEC.encode(buf, Optional.of(net.minecraft.network.chat.numbers.StyledFormat.NO_STYLE));
            }
            case SIDEBAR_DEFAULT -> {
                NumberFormatTypes.OPTIONAL_STREAM_CODEC.encode(buf, Optional.of(net.minecraft.network.chat.numbers.StyledFormat.SIDEBAR_DEFAULT));
            }
            case PLAYER_LIST_DEFAULT -> {
                NumberFormatTypes.OPTIONAL_STREAM_CODEC.encode(buf, Optional.of(net.minecraft.network.chat.numbers.StyledFormat.PLAYER_LIST_DEFAULT));
            }
        }
    }

    private ClientboundSetDisplayObjectivePacket newDisplaySlotPacket() {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeByte(Objects.requireNonNull(SLOT_POSITIONS.get(getSlot())));
        buf.writeUtf(this.getName());
        return ClientboundSetDisplayObjectivePacket.STREAM_CODEC.decode(buf);
    }

    private ClientboundResetScorePacket newResetScorePacket(String name) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUtf(name);
        buf.writeNullable(getName(), FriendlyByteBuf::writeUtf);
        return ClientboundResetScorePacket.STREAM_CODEC.decode(buf);
    }

    private ClientboundSetScorePacket newScorePacket(String name, ScoreValue scoreValue) {
        var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), MinecraftServer.getServer().registryAccess());
        buf.writeUtf(name);
        buf.writeUtf(this.getName());
        buf.writeVarInt(scoreValue.value());
        PacketUtil.writeComponentOptional(buf, scoreValue.display());
        encodeStyledFormat(buf, scoreValue.styledFormat());
        return ClientboundSetScorePacket.STREAM_CODEC.decode(buf);
    }
}
