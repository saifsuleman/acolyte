package net.odinmc.essentials.module.scoreboard;

import java.util.List;
import java.util.function.Function;
import lombok.Builder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@Getter
@Builder
public class ScoreboardConfig {

    private List<Component> titles;
    private Function<Player, List<Component>> lines;
    private long titleUpdateFrequency;
}
