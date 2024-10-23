package net.odinmc.core.paper.scoreboard.interfaces;

import java.util.Collection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.odinmc.core.common.scoreboard.ScoreboardTeam;
import org.bukkit.scoreboard.Team;

public interface PaperScoreboardTeam extends ScoreboardTeam {
    PaperScoreboard getScoreboard();

    Component getTitle();

    void setTitle(Component title);

    Component getPrefix();

    void setPrefix(Component prefix);

    Component getSuffix();

    void setSuffix(Component suffix);

    Team.OptionStatus getNameTagVisibility();

    void setNameTagVisibility(Team.OptionStatus nameTagVisibility);

    TextColor getColor();

    void setColor(TextColor color);

    Collection<String> getEntries();

    boolean hasEntry(String entry);

    void addEntry(String entry);

    boolean removeEntry(String entry);
}
