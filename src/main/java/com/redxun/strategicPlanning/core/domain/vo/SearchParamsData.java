package com.redxun.strategicPlanning.core.domain.vo;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public class SearchParamsData implements Serializable {
    private static final long serialVersionUID = 1193845686264572606L;
    private String name;
    private String value;

    public SearchParamsData() {
    }

    public SearchParamsData(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("value", value)
                .toString();
    }
}