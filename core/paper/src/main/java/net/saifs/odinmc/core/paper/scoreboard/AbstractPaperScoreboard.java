package net.saifs.odinmc.core.paper.scoreboard;

import com.google.common.base.Preconditions;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.kyori.adventure.text.Component;
import net.odinmc.core.common.util.ConcurrentHashSet;
import net.saifs.odinmc.core.paper.scoreboard.interfaces.PaperScoreboard;
import net.saifs.odinmc.core.paper.scoreboard.interfaces.PaperScoreboardObjective;
import net.saifs.odinmc.core.paper.scoreboard.interfaces.PaperScoreboardTeam;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public abstract class AbstractPaperScoreboard implements PaperScoreboard {

    private final Set<Player> subscribers = new ConcurrentHashSet<>();
    private final Map<String, PaperScoreboardObjective> objectives = new ConcurrentHashMap<>();
    private final Map<Player, Map<String, PaperScoreboardObjective>> playerObjectives = new ConcurrentHashMap<>();
    private final Map<String, PaperScoreboardTeam> teams = new ConcurrentHashMap<>();
    private final Map<Player, Map<String, PaperScoreboardTeam>> playerTeams = new ConcurrentHashMap<>();

    @Override
    public boolean isSubscribed(Player player) {
        return subscribers.contains(player);
    }

    @Override
    public void subscribe(Player player) {
        if (!subscribers.add(player)) {
            return;
        }
        for (var objective : objectives.values()) {
            ((AbstractPaperScoreboardObjective) objective).subscribe(player);
        }
        for (var team : teams.values()) {
            ((AbstractPaperScoreboardTeam) team).subscribe(player);
        }
    }

    @Override
    public void unsubscribe(Player player, boolean disconnected) {
        if (!subscribers.remove(player)) {
            return;
        }
        for (var objective : objectives.values()) {
            ((AbstractPaperScoreboardObjective) objective).unsubscribe(player, disconnected);
        }
        var objectives = playerObjectives.remove(player);
        if (objectives != null && !disconnected) {
            for (var objective : objectives.values()) {
                ((AbstractPaperScoreboardObjective) objective).unsubscribe(player, false);
            }
        }
        for (var team : teams.values()) {
            ((AbstractPaperScoreboardTeam) team).unsubscribe(player, disconnected);
        }
        var teams = playerTeams.remove(player);
        if (teams != null && !disconnected) {
            for (var team : teams.values()) {
                ((AbstractPaperScoreboardTeam) team).unsubscribe(player, false);
            }
        }
    }

    @Override
    public Collection<PaperScoreboardObjective> getObjectives() {
        return objectives.values();
    }

    @Override
    public PaperScoreboardObjective getObjective(String name) {
        return objectives.get(name);
    }

    @Override
    public PaperScoreboardObjective newObjective(String name, Component title, String criteria, DisplaySlot slot, NumberFormat styledFormat) {
        Preconditions.checkState(!objectives.containsKey(name), "objective already exists");
        var objective = buildObjective(name, title, criteria, slot, styledFormat);
        objectives.put(name, objective);
        for (var player : subscribers) {
            objective.subscribe(player);
        }
        return objective;
    }

    @Override
    public boolean removeObjective(String name) {
        var objective = (AbstractPaperScoreboardObjective) objectives.remove(name);
        if (objective == null) {
            return false;
        }
        objective.unsubscribeAll();
        return true;
    }

    @Override
    public Collection<PaperScoreboardObjective> getPlayerObjectives(Player player) {
        var objectives = playerObjectives.get(player);
        if (objectives == null) {
            return Collections.emptyList();
        }
        return objectives.values();
    }

    @Override
    public PaperScoreboardObjective getPlayerObjective(Player player, String name) {
        var objectives = playerObjectives.get(player);
        if (objectives == null) {
            return null;
        }
        return objectives.get(name);
    }

    @Override
    public PaperScoreboardObjective newPlayerObjective(
        Player player,
        String name,
        Component title,
        String criteria,
        DisplaySlot slot,
        NumberFormat styledFormat
    ) {
        var objectives = playerObjectives.get(player);
        if (objectives == null) {
            objectives = new HashMap<>();
            playerObjectives.put(player, objectives);
        } else {
            Preconditions.checkState(!objectives.containsKey(name), "player objective already exists");
        }
        var objective = buildObjective(name, title, criteria, slot, styledFormat);
        objectives.put(name, objective);
        objective.subscribe(player);
        return objective;
    }

    @Override
    public boolean removePlayerObjective(Player player, String name) {
        var objectives = playerObjectives.get(player);
        if (objectives == null) {
            return false;
        }
        var objective = (AbstractPaperScoreboardObjective) objectives.remove(name);
        if (objective == null) {
            return false;
        }
        if (objectives.isEmpty()) {
            playerObjectives.remove(player);
        }
        objective.unsubscribeAll();
        return true;
    }

    @Override
    public Collection<PaperScoreboardTeam> getTeams() {
        return teams.values();
    }

    @Override
    public PaperScoreboardTeam getTeam(String name) {
        return teams.get(name);
    }

    @Override
    public PaperScoreboardTeam newTeam(String name, Component title) {
        Preconditions.checkState(!teams.containsKey(name), "team already exists");
        var team = buildTeam(name, title);
        teams.put(name, team);
        for (var player : subscribers) {
            team.subscribe(player);
        }
        return team;
    }

    @Override
    public boolean removeTeam(String name) {
        var team = (AbstractPaperScoreboardTeam) teams.remove(name);
        if (team == null) {
            return false;
        }
        team.unsubscribeAll();
        return true;
    }

    @Override
    public Collection<PaperScoreboardTeam> getPlayerTeams(Player player) {
        var teams = playerTeams.get(player);
        if (teams == null) {
            return Collections.emptyList();
        }
        return teams.values();
    }

    @Override
    public PaperScoreboardTeam getPlayerTeam(Player player, String name) {
        var teams = playerTeams.get(player);
        if (teams == null) {
            return null;
        }
        return teams.get(name);
    }

    @Override
    public PaperScoreboardTeam newPlayerTeam(Player player, String name, Component title) {
        var teams = playerTeams.get(player);
        if (teams == null) {
            teams = new HashMap<>();
            playerTeams.put(player, teams);
        } else {
            Preconditions.checkState(!teams.containsKey(name), "player team already exists");
        }
        var team = buildTeam(name, title);
        teams.put(name, team);
        team.subscribe(player);
        return team;
    }

    @Override
    public boolean removePlayerTeam(Player player, String name) {
        var teams = playerTeams.get(player);
        if (teams == null) {
            return false;
        }
        var team = (AbstractPaperScoreboardTeam) teams.remove(name);
        if (team == null) {
            return false;
        }
        if (teams.isEmpty()) {
            playerTeams.remove(player);
        }
        team.unsubscribeAll();
        return true;
    }

    protected abstract AbstractPaperScoreboardObjective buildObjective(
        String name,
        Component title,
        String criteria,
        DisplaySlot slot,
        NumberFormat styledFormat
    );

    protected abstract AbstractPaperScoreboardTeam buildTeam(String name, Component title);
}
