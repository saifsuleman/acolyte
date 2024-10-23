package net.saifs.odinmc.core.paper.scoreboard;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.odinmc.core.common.util.StringUtil;
import net.saifs.odinmc.core.paper.scoreboard.interfaces.PaperScoreboard;
import net.saifs.odinmc.core.paper.scoreboard.interfaces.PaperScoreboardObjective;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractPaperScoreboardObjective implements PaperScoreboardObjective {

    private static final int MAX_NAME_LENGTH = 16;
    private static final int MAX_SCORE_NAME_LENGTH = 40;

    private final PaperScoreboard scoreboard;
    private final String name;
    private final Map<String, ScoreValue> scores = new ConcurrentHashMap<>();
    private Component title;
    private DisplaySlot slot;
    private NumberFormat styledFormat;

    public AbstractPaperScoreboardObjective(PaperScoreboard scoreboard, String name, Component title, DisplaySlot slot, NumberFormat numberFormat) {
        Preconditions.checkState(name.length() <= MAX_NAME_LENGTH);
        this.scoreboard = scoreboard;
        this.name = name;
        this.title = title;
        this.slot = slot;
        this.styledFormat = numberFormat;
    }

    @Override
    public PaperScoreboard getScoreboard() {
        return scoreboard;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public void setTitle(Component title) {
        if (this.title.equals(title)) {
            return;
        }
        this.title = title;
        updateTitle(title);
    }

    @Override
    public DisplaySlot getSlot() {
        return slot;
    }

    @Override
    public void setSlot(DisplaySlot slot) {
        if (this.slot.equals(slot)) {
            return;
        }
        this.slot = slot;
        updateSlot(slot);
    }

    @Override
    public Map<String, ScoreValue> getScores() {
        return scores;
    }

    public NumberFormat getStyledFormat() {
        return styledFormat;
    }

    public void setStyledFormat(NumberFormat styledFormat) {
        if (this.styledFormat == styledFormat) {
            return;
        }

        this.styledFormat = styledFormat;
        this.updateStyledFormat(styledFormat);
    }

    @Override
    public boolean hasScore(String name) {
        name = StringUtil.limit(name, MAX_SCORE_NAME_LENGTH);
        return scores.containsKey(name);
    }

    @Override
    public ScoreValue getScore(String name) {
        name = StringUtil.limit(name, MAX_SCORE_NAME_LENGTH);
        return scores.get(name);
    }

    @Override
    public void setScore(String name, int value, Component display, NumberFormat styledFormat) {
        name = StringUtil.limit(name, MAX_SCORE_NAME_LENGTH);
        var scoreValue = new ScoreValue(value, display, styledFormat);
        if (Objects.equals(scores.put(name, scoreValue), scoreValue)) {
            return;
        }
        updateScore(name, scoreValue);
    }

    @Override
    public boolean removeScore(String name) {
        name = StringUtil.limit(name, MAX_SCORE_NAME_LENGTH);
        if (scores.remove(name) == null) {
            return false;
        }
        updateRemove(name);
        return true;
    }

    @Override
    public void clearScores() {
        if (scores.isEmpty()) {
            return;
        }
        scores.clear();
        updateClear();
    }

    @Override
    public int getMaxScoreNameLength() {
        return MAX_SCORE_NAME_LENGTH;
    }

    protected abstract void subscribe(Player player);

    protected abstract void unsubscribe(Player player, boolean disconnected);

    protected abstract void unsubscribeAll();

    protected abstract void updateTitle(Component title);

    protected abstract void updateSlot(DisplaySlot slot);

    protected abstract void updateScore(String name, ScoreValue value);

    protected abstract void updateRemove(String name);

    protected abstract void updateClear();

    protected abstract void updateStyledFormat(NumberFormat styledFormat);

}
