package net.odinmc.core.common.scoreboard;

public interface ScoreboardObjective {

  Scoreboard getScoreboard();

  String getName();

  void clearScores();

  int getMaxScoreNameLength();
}
