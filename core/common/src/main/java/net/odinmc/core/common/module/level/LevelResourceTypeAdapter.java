package net.odinmc.core.common.module.level;

import com.google.gson.*;
import java.lang.reflect.Type;

public class LevelResourceTypeAdapter implements JsonSerializer<LevelResource>, JsonDeserializer<LevelResource> {

    private final LevelModule levelModule;

    public LevelResourceTypeAdapter(LevelModule levelModule) {
        this.levelModule = levelModule;
    }

    @Override
    public JsonElement serialize(LevelResource resource, Type type, JsonSerializationContext context) {
        if (resource instanceof Point) {
            var object = new JsonObject();
            object.addProperty("type", "point");
            object.addProperty("x", ((Point) resource).getX());
            object.addProperty("y", ((Point) resource).getY());
            object.addProperty("z", ((Point) resource).getZ());
            return object;
        } else if (resource instanceof PointSet) {
            var object = new JsonObject();
            object.addProperty("type", "pointset");
            var set = new JsonObject();
            for (var entry : ((PointSet) resource).getAll().entrySet()) {
                var pointObject = new JsonObject();
                var point = entry.getValue();
                pointObject.addProperty("x", point.getX());
                pointObject.addProperty("y", point.getY());
                pointObject.addProperty("z", point.getZ());
                set.add(entry.getKey(), pointObject);
            }
            object.add("set", set);
            return object;
        } else if (resource instanceof Region) {
            var object = new JsonObject();
            object.addProperty("type", "region");
            object.addProperty("x1", ((Region) resource).getMinPoint().getX());
            object.addProperty("y1", ((Region) resource).getMinPoint().getY());
            object.addProperty("z1", ((Region) resource).getMinPoint().getZ());
            object.addProperty("x2", ((Region) resource).getMaxPoint().getX());
            object.addProperty("y2", ((Region) resource).getMaxPoint().getY());
            object.addProperty("z2", ((Region) resource).getMaxPoint().getZ());
            return object;
        } else if (resource instanceof RegionSet) {
            var object = new JsonObject();
            object.addProperty("type", "regionset");
            var set = new JsonObject();
            for (var entry : ((RegionSet) resource).getAll().entrySet()) {
                var regionObject = new JsonObject();
                var region = entry.getValue();
                regionObject.addProperty("x1", region.getMinPoint().getX());
                regionObject.addProperty("y1", region.getMinPoint().getY());
                regionObject.addProperty("z1", region.getMinPoint().getZ());
                regionObject.addProperty("x2", region.getMaxPoint().getX());
                regionObject.addProperty("y2", region.getMaxPoint().getY());
                regionObject.addProperty("z2", region.getMaxPoint().getZ());
                set.add(entry.getKey(), regionObject);
            }
            object.add("set", set);
            return object;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public LevelResource deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        var object = element.getAsJsonObject();
        var resourceType = object.get("type").getAsString();
        if (resourceType.equals("point")) {
            return levelModule.newPoint(object.get("x").getAsInt(), object.get("y").getAsInt(), object.get("z").getAsInt());
        } else if (resourceType.equals("pointset")) {
            var set = object.get("set").getAsJsonObject();
            var pointSet = new PointSet();
            for (var entry : set.entrySet()) {
                var pointObject = entry.getValue().getAsJsonObject();
                pointSet.add(
                    entry.getKey(),
                    levelModule.newPoint(pointObject.get("x").getAsInt(), pointObject.get("y").getAsInt(), pointObject.get("z").getAsInt())
                );
            }
            return pointSet;
        } else if (resourceType.equals("region")) {
            return levelModule.newRegion(
                levelModule.newPoint(object.get("x1").getAsInt(), object.get("y1").getAsInt(), object.get("z1").getAsInt()),
                levelModule.newPoint(object.get("x2").getAsInt(), object.get("y2").getAsInt(), object.get("z2").getAsInt())
            );
        } else if (resourceType.equals("regionset")) {
            var set = object.get("set").getAsJsonObject();
            var regionSet = new RegionSet();
            for (var entry : set.entrySet()) {
                var pointObject = entry.getValue().getAsJsonObject();
                regionSet.add(
                    entry.getKey(),
                    levelModule.newRegion(
                        levelModule.newPoint(pointObject.get("x1").getAsInt(), pointObject.get("y1").getAsInt(), pointObject.get("z1").getAsInt()),
                        levelModule.newPoint(pointObject.get("x2").getAsInt(), pointObject.get("y2").getAsInt(), pointObject.get("z2").getAsInt())
                    )
                );
            }
            return regionSet;
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
