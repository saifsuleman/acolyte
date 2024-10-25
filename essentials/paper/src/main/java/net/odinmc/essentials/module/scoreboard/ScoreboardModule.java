package net.odinmc.essentials.module.scoreboard;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import net.kyori.adventure.text.Component;
import net.odinmc.core.common.scheduling.Schedulers;
import net.odinmc.core.common.services.Services;
import net.odinmc.core.common.terminable.TerminableConsumer;
import net.odinmc.core.common.terminable.module.TerminableModule;
import net.odinmc.core.paper.events.Events;
import net.odinmc.core.paper.scoreboard.NumberFormat;
import net.odinmc.core.paper.scoreboard.interfaces.PaperScoreboard;
import net.odinmc.core.paper.scoreboard.interfaces.PaperScoreboardObjective;
import net.odinmc.core.paper.store.AsyncPlayerStore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;

public class ScoreboardModule implements TerminableModule {

    private final AsyncPlayerStore players = Services.load(AsyncPlayerStore.class);
    private final PaperScoreboard scoreboard = Services.load(PaperScoreboard.class);
    private final ScoreboardConfig config = Services.load(ScoreboardConfig.class);
    private final Map<UUID, PaperScoreboardObjective> objectiveMap = new ConcurrentHashMap<>();

    @Override
    public void setup(TerminableConsumer consumer) {
        Events.subscribe(PlayerJoinEvent.class).handler(event -> Schedulers.async().run(() -> show(event.getPlayer()))).bindWith(consumer);
        Events.subscribe(PlayerQuitEvent.class).handler(event -> Schedulers.async().run(() -> hide(event.getPlayer()))).bindWith(consumer);

        for (var player : players) {
            show(player);
        }

        Schedulers.async().scheduleRepeating(this::updateAll, 10L).bindWith(consumer);

        var cyclingTitleIndex = new AtomicLong(0);
        Schedulers
            .async()
            .scheduleRepeating(
                () -> {
                    var titleI = cyclingTitleIndex.incrementAndGet();
                    if (titleI % config.getTitleUpdateFrequency() != 0) {
                        return;
                    }
                    var title = config.getTitles().get((int) ((titleI / config.getTitleUpdateFrequency()) % config.getTitles().size()));
                    for (var entry : objectiveMap.entrySet()) {
                        var objective = entry.getValue();
                        if (objective != null) {
                            objective.setTitle(title);
                        }
                    }
                },
                1
            );

        consumer.bind(() -> {
            for (var player : players) {
                hide(player);
            }
        });
    }

    public void show(Player player) {
        var uuid = player.getUniqueId();
        if (objectiveMap.containsKey(uuid)) {
            return;
        }

        var objective = scoreboard.newPlayerObjective(
            player,
            UUID.randomUUID().toString().substring(0, 15),
            Component.empty(),
            "dummy",
            DisplaySlot.SIDEBAR
        );

        objectiveMap.put(uuid, objective);
        update(player, objective);
    }

    public void hide(Player player) {
        var uuid = player.getUniqueId();
        var objective = objectiveMap.remove(uuid);
        if (objective != null) {
            scoreboard.removePlayerObjective(player, objective.getName());
        }
    }

    public void update(Player player) {
        var objective = objectiveMap.get(player.getUniqueId());
        if (objective == null) {
            return;
        }
        update(player, objective);
    }

    public void updateAll() {
        for (var entry : objectiveMap.entrySet()) {
            var player = Bukkit.getPlayer(entry.getKey());
            update(player, entry.getValue());
        }
    }

    private void update(Player player, PaperScoreboardObjective objective) {
        if (!scoreboard.isSubscribed(player)) {
            return;
        }

        var lines = config.getLines().apply(player);
        if (lines == null) {
            return;
        }
        setScoreboardLines(player, objective, lines);
    }

    @SuppressWarnings("deprecation")
    private void setScoreboardLines(Player player, PaperScoreboardObjective objective, List<Component> lines) {
        for (var i = 0; i < Math.min(lines.size(), 15); i++) {
            var line = lines.get(i);
            var color = ChatColor.values()[i].toString();
            var team = scoreboard.getPlayerTeam(player, color);
            if (team == null) {
                team = scoreboard.newPlayerTeam(player, color, Component.empty());
                team.addEntry(color);
            }
            team.setPrefix(Component.empty());
            objective.setScore(color, lines.size() - i, line, NumberFormat.BLANK);
        }

        if (lines.size() >= 15) {
            return;
        }

        for (var i = lines.size(); i < 15; i++) {
            var color = ChatColor.values()[i].toString();
            objective.removeScore(color);
            scoreboard.removePlayerTeam(player, color);
        }
    }
}
