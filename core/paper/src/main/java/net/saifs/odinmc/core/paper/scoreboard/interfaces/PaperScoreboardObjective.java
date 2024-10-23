package net.saifs.odinmc.core.paper.scoreboard.interfaces;

import java.util.Map;
import net.kyori.adventure.text.Component;
import net.odinmc.core.common.scoreboard.ScoreboardObjective;
import net.saifs.odinmc.core.paper.scoreboard.NumberFormat;
import org.bukkit.scoreboard.DisplaySlot;

public interface PaperScoreboardObjective extends ScoreboardObjective {
    PaperScoreboard getScoreboard();

    Component getTitle();

    void setTitle(Component title);

    DisplaySlot getSlot();

    void setSlot(DisplaySlot slot);

    Map<String, ScoreValue> getScores();

    boolean hasScore(String name);

    ScoreValue getScore(String name);

    void setScore(String name, int value, Component display, NumberFormat styledFormat);

    boolean removeScore(String name);

    record ScoreValue(int value, Component display, NumberFormat styledFormat) {}
}
