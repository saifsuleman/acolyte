package net.odinmc.essentials.module.scoreboard;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class ScoreboardConfig {

    private List<Component> titles = Collections.emptyList();
    private Function<Player, List<Component>> lines = player -> Collections.emptyList();
    private long titleUpdateFrequency = 5L;

    public List<Component> getTitles() {
        return titles;
    }

    public void setTitles(List<Component> titles) {
        this.titles = titles;
    }

    public Function<Player, List<Component>> getLines() {
        return lines;
    }

    public void setLines(Function<Player, List<Component>> lines) {
        this.lines = lines;
    }

    public long getTitleUpdateFrequency() {
        return titleUpdateFrequency;
    }

    public void setTitleUpdateFrequency(long titleUpdateFrequency) {
        this.titleUpdateFrequency = titleUpdateFrequency;
    }
}
