package net.odinmc.core.common.module.level;

public interface Point extends LevelResource {
    Point min(Point point);

    Point max(Point point);

    Point center();

    Point ceil();

    Point floor();

    Point offset(double v);

    Point offset(double x, double y, double z);

    default double distance(double x, double y, double z) {
        return Math.sqrt(distanceSquared(x, y, z));
    }

    default double distance(Point point) {
        return Math.sqrt(distanceSquared(point));
    }

    default double distanceSquared(double x, double y, double z) {
        return Math.pow(getX() - x, 2) + Math.pow(getY() - y, 2) + Math.pow(getZ() - z, 2);
    }

    default double distanceSquared(Point point) {
        return distanceSquared(point.getX(), point.getY(), point.getZ());
    }

    default boolean lessThan(double x, double y, double z) {
        return getX() < x || getY() < y || getZ() < z;
    }

    default boolean lessThan(Point point) {
        return lessThan(point.getX(), point.getY(), point.getZ());
    }

    default boolean moreThan(double x, double y, double z) {
        return getX() > x || getY() > y || getZ() > z;
    }

    default boolean moreThan(Point point) {
        return moreThan(point.getX(), point.getY(), point.getZ());
    }

    int getChunkX();

    int getChunkZ();

    int floorChunkX();

    int floorChunkZ();

    int ceilChunkX();

    int ceilChunkZ();

    double getX();

    double getY();

    double getZ();
}
