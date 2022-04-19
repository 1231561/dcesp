package com.qin.dcesp.entity.messageclass;

public class NodeDataFromFront {
    private String key;
    private String category;
    private int __gohashid;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int get__gohashid() {
        return __gohashid;
    }

    public void set__gohashid(int __gohashid) {
        this.__gohashid = __gohashid;
    }

    public NodeDataFromFront(String key, String category, int __gohashid) {
        this.key = key;
        this.category = category;
        this.__gohashid = __gohashid;
    }

    public NodeDataFromFront() {
    }
}
