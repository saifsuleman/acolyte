package net.odinmc.core.common.module.data;

import net.odinmc.core.common.scheduling.Promise;

import java.util.List;

public class ServerGroupData {
    private final DataModule dataModule;
    private final String like;

    public ServerGroupData(DataModule dataModule, String like) {
        this.dataModule = dataModule;
        this.like = like;
    }

    public Promise<List<ServerData>> getServers() {
        return dataModule.getServersLike(this.like);
    }
}
