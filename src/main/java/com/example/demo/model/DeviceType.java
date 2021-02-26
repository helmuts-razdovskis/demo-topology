package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DeviceType {
    GATEWAY("Gateway"),
    SWITCH("Switch"),
    ACCESS_POINT("Access Point");

    private final String value;

    private DeviceType(final String description) {
        this.value = description;
    }

    private DeviceType() {
        this.value = this.name();
    }

    @JsonValue
    final String value() {
        return this.value;
    }
}
