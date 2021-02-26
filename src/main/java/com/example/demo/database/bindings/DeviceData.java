package com.example.demo.database.bindings;

import java.util.ArrayList;
import java.util.List;

public class DeviceData {
    public static final long NULL_MAC = 0xFFFFFFFFFFFFFFFFL;

    private byte deviceType;
    private long address;
    private long uplink;
    private List<Long> children;

    public byte getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(byte deviceType) {
        this.deviceType = deviceType;
    }

    public long getAddress() {
        return address;
    }

    public void setAddress(long address) {
        this.address = address;
    }

    public long getUplink() {
        return uplink;
    }

    public void setUplink(long uplink) {
        this.uplink = uplink;
    }

    public List<Long> getChildren() {
        if (children == null)
            children = new ArrayList<>();
        return children;
    }

    public void setChildren(List<Long> children) {
        this.children = children;
    }

}
