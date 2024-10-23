package net.saifs.odinmc.core.paper.scoreboard;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.odinmc.core.common.util.ConcurrentHashSet;
import net.saifs.odinmc.core.paper.scoreboard.interfaces.PaperScoreboard;
import net.saifs.odinmc.core.paper.scoreboard.interfaces.PaperScoreboardTeam;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public abstract class AbstractPaperScoreboardTeam implements PaperScoreboardTeam {

    private static final int MAX_NAME_LENGTH = 16;

    private final PaperScoreboard scoreboard;
    private final String name;
    private Component title;
    private Component prefix = Component.empty();
    private Component suffix = Component.empty();
    private boolean friendlyFire = true;
    private boolean friendlyInvisibles = true;
    private Team.OptionStatus nameTagVisibility = Team.OptionStatus.ALWAYS;
    private TextColor color = NamedTextColor.WHITE;
    private final Set<String> entries = new ConcurrentHashSet<>();

    public AbstractPaperScoreboardTeam(PaperScoreboard scoreboard, String name, Component title) {
        Preconditions.checkState(name.length() <= MAX_NAME_LENGTH);
        this.scoreboard = scoreboard;
        this.name = name;
        this.title = title;
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
        if (title.equals(this.title)) {
            return;
        }
        this.title = title;
        updateTitle(title);
    }

    @Override
    public Component getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(Component prefix) {
        if (prefix.equals(this.prefix)) {
            return;
        }
        this.prefix = prefix;
        updatePrefix(prefix);
    }

    @Override
    public Component getSuffix() {
        return suffix;
    }

    @Override
    public void setSuffix(Component suffix) {
        if (suffix.equals(this.suffix)) {
            return;
        }
        this.suffix = suffix;
        updateSuffix(suffix);
    }

    @Override
    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    @Override
    public void setFriendlyFire(boolean friendlyFire) {
        if (friendlyFire == this.friendlyFire) {
            return;
        }
        this.friendlyFire = friendlyFire;
        updateFriendlyFire(friendlyFire);
    }

    @Override
    public boolean isFriendlyInvisibles() {
        return friendlyInvisibles;
    }

    @Override
    public void setFriendlyInvisibles(boolean friendlyInvisibles) {
        if (friendlyInvisibles == this.friendlyInvisibles) {
            return;
        }
        this.friendlyInvisibles = friendlyInvisibles;
        updateFriendlyInvisibles(friendlyInvisibles);
    }

    @Override
    public Team.OptionStatus getNameTagVisibility() {
        return nameTagVisibility;
    }

    @Override
    public void setNameTagVisibility(Team.OptionStatus nameTagVisibility) {
        if (nameTagVisibility.equals(this.nameTagVisibility)) {
            return;
        }
        this.nameTagVisibility = nameTagVisibility;
        updateNameTagVisibility(nameTagVisibility);
    }

    @Override
    public TextColor getColor() {
        return color;
    }

    @Override
    public void setColor(TextColor color) {
        if (color.equals(this.color)) {
            return;
        }
        this.color = color;
        updateColor(color);
    }

    @Override
    public Collection<String> getEntries() {
        return entries;
    }

    @Override
    public boolean hasEntry(String entry) {
        return entries.contains(entry);
    }

    @Override
    public void addEntry(String entry) {
        if (!entries.add(entry)) {
            return;
        }
        updateEntryAdd(entry);
    }

    @Override
    public boolean removeEntry(String entry) {
        if (!entries.remove(entry)) {
            return false;
        }
        updateEntryRemove(entry);
        return true;
    }

    protected abstract void subscribe(Player player);

    protected abstract void unsubscribe(Player player, boolean disconnected);

    protected abstract void unsubscribeAll();

    protected abstract void updateTitle(Component title);

    protected abstract void updatePrefix(Component prefix);

    protected abstract void updateSuffix(Component suffix);

    protected abstract void updateFriendlyFire(boolean friendlyFire);

    protected abstract void updateFriendlyInvisibles(boolean friendlyInvisibles);

    protected abstract void updateNameTagVisibility(Team.OptionStatus nameTagVisibility);

    protected abstract void updateColor(TextColor color);

    protected abstract void updateEntryAdd(String entry);

    protected abstract void updateEntryRemove(String entry);
}
