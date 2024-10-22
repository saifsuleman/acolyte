package net.saifs.odinmc.core.paper.store;

import java.util.concurrent.CopyOnWriteArrayList;
import net.odinmc.core.common.terminable.TerminableConsumer;
import net.odinmc.core.common.terminable.module.TerminableModule;
import net.saifs.odinmc.core.paper.events.Events;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AsyncPlayerStore extends CopyOnWriteArrayList<Player> implements TerminableModule {

    @Override
    public void setup(TerminableConsumer consumer) {
        Events.subscribe(PlayerJoinEvent.class, EventPriority.LOWEST).handler(event -> add(event.getPlayer())).bindWith(consumer);
        Events.subscribe(PlayerQuitEvent.class, EventPriority.MONITOR).handler(event -> remove(event.getPlayer())).bindWith(consumer);
        this.addAll(Bukkit.getServer().getOnlinePlayers());
        consumer.bind(this::clear);
    }
}
