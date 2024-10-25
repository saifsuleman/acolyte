package net.odinmc.core.common.module.level;

import java.util.List;
import java.util.Set;

public interface Region extends LevelResource {
    default boolean contains(double x, double y, double z) {
        if (getMinPoint().moreThan(x, y, z)) {
            return false;
        }
        if (getMaxPoint().lessThan(x, y, z)) {
            return false;
        }
        return true;
    }

    default boolean contains(Point point) {
        return contains(point.getX(), point.getY(), point.getZ());
    }

    default boolean intersects(Region region) {
        var regionMinPoint = region.getMinPoint();
        var regionMaxPoint = region.getMaxPoint();
        if (regionMaxPoint.lessThan(getMinPoint())) {
            return false;
        }
        if (regionMinPoint.moreThan(getMaxPoint())) {
            return false;
        }
        return true;
    }

    default double distance(double x, double y, double z) {
        return Math.sqrt(distanceSquared(x, y, z));
    }

    default double distance(Point point) {
        return Math.sqrt(distanceSquared(point));
    }

    default double distanceSquared(double x, double y, double z) {
        double ret = 0;
        var minPoint = getMinPoint();
        var maxPoint = getMaxPoint();
        if (x < minPoint.getX()) {
            ret += Math.pow(minPoint.getX() - x, 2);
        } else if (x > maxPoint.getX()) {
            ret += Math.pow(maxPoint.getX() - x, 2);
        }
        if (y < minPoint.getY()) {
            ret += Math.pow(minPoint.getY() - y, 2);
        } else if (y > maxPoint.getY()) {
            ret += Math.pow(maxPoint.getY() - y, 2);
        }
        if (z < minPoint.getZ()) {
            ret += Math.pow(minPoint.getZ() - z, 2);
        } else if (z > maxPoint.getZ()) {
            ret += Math.pow(maxPoint.getZ() - z, 2);
        }
        return ret;
    }

    default double distanceSquared(Point point) {
        return distanceSquared(point.getX(), point.getY(), point.getZ());
    }

    Point center();

    Region expand(double v);

    Region expand(double x, double y, double z);

    Region getBounds();

    Point getMinPoint();

    Point getMaxPoint();

    List<Point> getCorners();

    Set<Long> getChunks();
}
