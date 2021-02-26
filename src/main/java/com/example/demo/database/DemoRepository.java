package com.example.demo.database;

import java.util.List;

import com.example.demo.model.MacAddress;
import com.example.demo.model.Device;

public interface DemoRepository {

    Device find(MacAddress address);
    List<Device> findAll();
    List<Device> findChildren();
    Device findChildren(MacAddress start);

    void insert(Device entry);

}
