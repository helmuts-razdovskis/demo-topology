package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.List;

@JsonInclude(value=JsonInclude.Include.NON_EMPTY)
public class Device implements Serializable {

    private static final long serialVersionUID = -8264390562455797347L;
    @NotNull
    private DeviceType deviceType;
    @NotNull
    private MacAddress address;
    private MacAddress uplink;
    @Null
    private List<Device> children;

    public Device() {}

    public Device(DeviceType deviceType, MacAddress address) {
        this.setDeviceType(deviceType);
        this.setAddress(address);
    }

    public Device(DeviceType deviceType, MacAddress address, MacAddress uplink) {
        this.setDeviceType(deviceType);
        this.setAddress(address);
        this.setUplink(uplink);
    }

    public List<Device> getChildren() {
        return children;
    }

    public void setChildren(List<Device> children) {
        this.children = children;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public MacAddress getAddress() {
        return address;
    }

    public void setAddress(MacAddress address) {
        this.address = address;
    }

    public MacAddress getUplink() {
        return uplink;
    }

    public void setUplink(MacAddress uplink) {
        this.uplink = uplink;
    }
}
