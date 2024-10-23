package net.odinmc.core.common.module.data;

import java.net.InetAddress;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData extends ConcurrentHashMap<String, Object> {
    private final int id;
    private final UUID uuid;
    private final String registerName;
    private final long registerTime;
    private final InetAddress registerAddress;
    private final String registerServerAddress;
    private final String lastLoginName;
    private final long lastLoginTime;
    private final InetAddress lastLoginAddress;
    private final Long lastLogoutTime;
    private final String lastLoginServerAddress;

    public PlayerData(int id, UUID uuid, String registerName, long registerTime, InetAddress registerAddress, String registerServerAddress, String lastLoginName, long lastLoginTime, InetAddress lastLoginAddress, Long lastLogoutTime, String lastLoginServerAddress) {
        this.id = id;
        this.uuid = uuid;
        this.registerName = registerName;
        this.registerTime = registerTime;
        this.registerAddress = registerAddress;
        this.registerServerAddress = registerServerAddress;
        this.lastLoginName = lastLoginName;
        this.lastLoginTime = lastLoginTime;
        this.lastLoginAddress = lastLoginAddress;
        this.lastLogoutTime = lastLogoutTime;
        this.lastLoginServerAddress = lastLoginServerAddress;
    }

    public int id() {
        return this.id;
    }

    public UUID uuid() {
        return this.uuid;
    }

    public String registerName() {
        return this.registerName;
    }

    public long registerTime() {
        return this.registerTime;
    }

    public InetAddress registerAddress() {
        return this.registerAddress;
    }

    public String registerServerAddress() {
        return this.registerServerAddress;
    }

    public String lastLoginName() {
        return this.lastLoginName;
    }

    public long lastLoginTime() {
        return this.lastLoginTime;
    }

    public InetAddress lastLoginAddress() {
        return this.lastLoginAddress;
    }

    public Long lastLogoutTime() {
        return this.lastLogoutTime;
    }

    public String lastLoginServerAddress() {
        return this.lastLoginServerAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof PlayerData playerData)) {
            return false;
        }

        return Objects.equals(id, playerData.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
