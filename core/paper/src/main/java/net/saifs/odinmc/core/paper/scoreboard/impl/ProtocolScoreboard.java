package net.saifs.odinmc.core.paper.scoreboard.impl;

import net.kyori.adventure.text.Component;
import net.saifs.odinmc.core.paper.scoreboard.AbstractPaperScoreboard;
import net.saifs.odinmc.core.paper.scoreboard.AbstractPaperScoreboardObjective;
import net.saifs.odinmc.core.paper.scoreboard.AbstractPaperScoreboardTeam;
import net.saifs.odinmc.core.paper.scoreboard.NumberFormat;
import org.bukkit.scoreboard.DisplaySlot;

public class ProtocolScoreboard extends AbstractPaperScoreboard {

    @Override
    protected AbstractPaperScoreboardObjective buildObjective(
        String name,
        Component title,
        String criteria,
        DisplaySlot slot,
        NumberFormat styledFormat
    ) {
        return new ProtocolScoreboardObjective(this, name, title, criteria, slot, styledFormat);
    }

    @Override
    protected AbstractPaperScoreboardTeam buildTeam(String name, Component title) {
        return new ProtocolScoreboardTeam(this, name, title);
    }
}
