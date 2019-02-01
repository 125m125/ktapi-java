package de._125m125.kt.ktapi.core.entities;

public class ItemName {
    private final String id;
    private final String name;

    public ItemName(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
