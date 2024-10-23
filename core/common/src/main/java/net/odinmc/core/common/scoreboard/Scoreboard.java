package net.odinmc.core.common.scoreboard;

public interface Scoreboard {
    default boolean hasObjective(String name) {
        return getObjective(name) != null;
    }

    ScoreboardObjective getObjective(String name);

    boolean removeObjective(String name);

    default boolean hasTeam(String name) {
        return getTeam(name) != null;
    }

    ScoreboardTeam getTeam(String name);

    boolean removeTeam(String name);
}
