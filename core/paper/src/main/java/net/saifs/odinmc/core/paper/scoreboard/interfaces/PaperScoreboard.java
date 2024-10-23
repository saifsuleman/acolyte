package net.saifs.odinmc.core.paper.scoreboard.interfaces;

import net.kyori.adventure.text.Component;
import net.odinmc.core.common.scoreboard.Scoreboard;
import net.odinmc.core.common.util.StringUtil;
import net.saifs.odinmc.core.paper.scoreboard.NumberFormat;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Collection;

public interface PaperScoreboard extends Scoreboard {

  boolean isSubscribed(Player player);

  void subscribe(Player player);

  default void unsubscribe(Player player) {
    unsubscribe(player, false);
  }

  void unsubscribe(Player player, boolean disconnected);

  Collection<PaperScoreboardObjective> getObjectives();

  @Override
  PaperScoreboardObjective getObjective(String name);

  default PaperScoreboardObjective newObjective(Component title) {
    return newObjective(title, DisplaySlot.SIDEBAR);
  }

  default PaperScoreboardObjective newObjective(DisplaySlot slot) {
    return newObjective(Component.empty(), slot);
  }

  default PaperScoreboardObjective newObjective(Component title, DisplaySlot slot) {
    return newObjective(StringUtil.limit(Long.toString(System.nanoTime()), 16), title, slot);
  }

  default PaperScoreboardObjective newObjective(String name, Component title, String criteria) {
    return newObjective(name, title, criteria, DisplaySlot.SIDEBAR);
  }

  default PaperScoreboardObjective newObjective(String name, Component title, DisplaySlot slot) {
    return newObjective(name, title, "dummy", slot);
  }

  default PaperScoreboardObjective newObjective(String name, Component title, String criteria, DisplaySlot slot) {
    return newObjective(name, title, "dummy", slot, NumberFormat.BLANK);
  }

  PaperScoreboardObjective newObjective(String name, Component title, String criteria, DisplaySlot slot, NumberFormat styledFormat);

  @Override
  boolean removeObjective(String name);

  Collection<PaperScoreboardObjective> getPlayerObjectives(Player player);

  default boolean hasPlayerObjective(Player player, String name) {
    return getPlayerObjective(player, name) != null;
  }

  PaperScoreboardObjective getPlayerObjective(Player player, String name);

  default PaperScoreboardObjective newPlayerObjective(Player player, Component title) {
    return newPlayerObjective(player, title, DisplaySlot.SIDEBAR);
  }

  default PaperScoreboardObjective newPlayerObjective(Player player, DisplaySlot slot) {
    return newPlayerObjective(player, Component.empty(), slot);
  }

  default PaperScoreboardObjective newPlayerObjective(Player player, Component title, DisplaySlot slot) {
    return newPlayerObjective(player, StringUtil.limit(Long.toString(System.nanoTime()), 16), title, slot);
  }

  default PaperScoreboardObjective newPlayerObjective(Player player, String name, Component title, String criteria) {
    return newPlayerObjective(player, name, title, criteria, DisplaySlot.SIDEBAR);
  }

  default PaperScoreboardObjective newPlayerObjective(Player player, String name, Component title, DisplaySlot slot) {
    return newPlayerObjective(player, name, title, "dummy", slot);
  }

  default PaperScoreboardObjective newPlayerObjective(Player player, String name, Component title, String criteria, DisplaySlot slot) {
    return newPlayerObjective(player, name, title, criteria, slot, NumberFormat.BLANK);
  }

  PaperScoreboardObjective newPlayerObjective(Player player, String name, Component title, String criteria, DisplaySlot slot, NumberFormat styledFormat);

  boolean removePlayerObjective(Player player, String name);

  Collection<PaperScoreboardTeam> getTeams();

  @Override
  PaperScoreboardTeam getTeam(String name);

  default PaperScoreboardTeam newTeam(Component title) {
    return newTeam(StringUtil.limit(Long.toString(System.nanoTime()), 16), title);
  }

  PaperScoreboardTeam newTeam(String name, Component title);

  @Override
  boolean removeTeam(String name);

  Collection<PaperScoreboardTeam> getPlayerTeams(Player player);

  default boolean hasPlayerTeam(Player player, String name) {
    return getPlayerTeam(player, name) != null;
  }

  PaperScoreboardTeam getPlayerTeam(Player player, String name);

  default PaperScoreboardTeam newPlayerTeam(Player player, Component title) {
    return newPlayerTeam(player, StringUtil.limit(Long.toString(System.nanoTime()), 16), title);
  }

  PaperScoreboardTeam newPlayerTeam(Player player, String name, Component title);

  boolean removePlayerTeam(Player player, String name);
}
