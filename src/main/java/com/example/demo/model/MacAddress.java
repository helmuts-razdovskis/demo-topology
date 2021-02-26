package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonValue;

public class MacAddress {

    private long value;

    public MacAddress(String value) {
        if (value == null || value.trim().isEmpty())
            throw new IllegalArgumentException("Empty value");
        String mac = value.replaceAll("[^a-fA-F0-9]", "");
        if (mac.length() != 12)
            throw new IllegalArgumentException("Invalid MAC address value: " + value);

        this.value = Long.parseLong(mac, 16);
    }

    public MacAddress(long value) {
        if (value != (value & 0xFFFFFFFFFFFFL))
            throw new IllegalArgumentException("Invalid MAC address value: " + Long.toHexString(value));
        this.value = value;
    }

    public static MacAddress parseString(String value) {
        return new MacAddress(value);
    }

    public long getValue() { return this.value; }

    @JsonValue
    public String  toString() {
        return String.format("%012x", this.value);
    }
}
