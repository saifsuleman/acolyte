package net.odinmc.core.common.module.menu;

import java.util.ArrayList;
import java.util.List;

public class MenuPartitionMask {

    private final String mask;
    private final int rows;
    private final List<Integer> slots = new ArrayList<>();
    private boolean extend = false;

    public MenuPartitionMask(String mask) {
        this.mask = mask;
        var row = 0;
        var slot = 0;
        for (var c : mask.toCharArray()) {
            if (this.extend) {
                throw new IllegalStateException();
            }
            switch (c) {
                case '0':
                    slot++;
                    break;
                case '1':
                    this.slots.add(slot++ + (row * 9));
                    break;
                case ' ':
                    row++;
                    slot = 0;
                    break;
                case '*':
                    this.extend = true;
                    break;
            }
            if (slot > 9) {
                throw new IllegalStateException();
            }
            if (row > 6) {
                throw new IllegalStateException();
            }
        }
        this.rows = row + 1;
    }

    public int getRows() {
        return rows;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public boolean isExtend() {
        return extend;
    }
}
