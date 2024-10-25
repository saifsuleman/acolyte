package net.odinmc.core.common.module.menu;

public abstract class AbstractMenuPartition implements MenuPartition {

    protected final MenuPartitionMask mask;

    protected AbstractMenuPartition(MenuPartitionMask mask) {
        this.mask = mask;
    }

    @Override
    public MenuPartitionMask getMask() {
        return mask;
    }
}
