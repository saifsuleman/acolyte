package net.odinmc.core.common.module.level;

import java.util.LinkedHashMap;
import java.util.Map;

public class LevelResourceSet<T extends LevelResource> {

    private final Map<String, T> resources = new LinkedHashMap<>();

    // FIXME: Temporary solution to maintain old api compat
    private String name;
    private Level parentLevel;

    public boolean contains(String name) {
        return resources.containsKey(name);
    }

    public T get(String name) {
        return resources.get(name);
    }

    public Map<String, T> getAll() {
        return resources;
    }

    public String add(T resource) {
        var name = getNextName();
        add(name, resource);
        return name;
    }

    public void add(String name, T resource) {
        if (resource == null) {
            throw new IllegalStateException("Null resources not supported");
        }
        if (resource instanceof LevelResourceSet && this instanceof LevelResource) {
            throw new IllegalStateException("Nested resource sets not supported");
        }
        var oldResource = resources.put(name, resource);
        if (oldResource == resource) {
            return;
        }
        if (oldResource != null) {
            onResourceRemove(name, oldResource);
        }
        onResourceAdd(name, resource);
        if (parentLevel != null) {
            parentLevel.onResourceModify(this.name, (LevelResource) this);
        }
    }

    public void addAll(Map<String, T> resources) {
        resources.entrySet().forEach(entry -> add(entry.getKey(), entry.getValue()));
    }

    public T remove(String name) {
        T resource = resources.remove(name);
        if (resource == null) {
            return null;
        }
        onResourceRemove(name, resource);
        if (parentLevel != null) {
            parentLevel.onResourceModify(this.name, (LevelResource) this);
        }
        return resource;
    }

    private String getNextName() {
        var i = 0;
        String string;
        do {
            string = String.valueOf(i++);
        } while (resources.containsKey(string));
        return string;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append("{");
        for (var entry : resources.entrySet()) {
            builder.append(entry.getKey());
            builder.append("=");
            builder.append(entry.getValue().toString());
            builder.append(",");
        }
        builder.append("}");
        return builder.toString();
    }

    protected void onResourceAdd(String resourceName, T resource) {}

    protected void onResourceRemove(String resourceName, T resource) {}

    protected String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected Level getParentLevel() {
        return parentLevel;
    }

    protected void setParentLevel(Level parentLevel) {
        this.parentLevel = parentLevel;
    }
}
