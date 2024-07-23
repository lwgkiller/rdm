package com.redxun.standardManager.core.model;

public enum StandardStatus {
    Draft("草稿", "draft"), Disable("已废止", "disable"), Enable("有效", "enable");

    private final String key;
    private final String name;

    private StandardStatus(String name, String key) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }
}
