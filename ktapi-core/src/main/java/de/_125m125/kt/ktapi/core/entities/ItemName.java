package de._125m125.kt.ktapi.core.entities;

public class ItemName {
    private String id;
    private String name;

    protected ItemName() {
        super();
    }

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
