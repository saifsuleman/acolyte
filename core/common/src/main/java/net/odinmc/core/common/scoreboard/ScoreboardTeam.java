package net.odinmc.core.common.scoreboard;

public interface ScoreboardTeam {

  Scoreboard getScoreboard();

  String getName();

  boolean isFriendlyFire();

  void setFriendlyFire(boolean friendlyFire);

  boolean isFriendlyInvisibles();

  void setFriendlyInvisibles(boolean friendlyInvisibles);
}
