package com.example.demo.controllers;

import com.example.demo.database.DemoRepository;
import com.example.demo.model.Device;
import com.example.demo.model.MacAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController()
public class DemoApiController {

    @Autowired
    private DemoRepository repository;

    @GetMapping("/devices")
    public List<Device> findAll() {

        return repository.findAll();
    }

    @GetMapping("/devices/{mac}")
    public ResponseEntity<Device> find(@PathVariable String mac) {

        Device device = repository.find(MacAddress.parseString(mac));
        if (device == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(device);
    }

    @GetMapping(value = "/topology")
    public List<Device> getTopology() {

        return repository.findChildren();
    }

    @GetMapping(value = "/topology/{mac}")
    public ResponseEntity<Device> getTopology(@PathVariable Optional<String> mac) {

        Device device =  repository.findChildren(MacAddress.parseString(mac.get()));
        if (device == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(device);
    }

    @PostMapping(value = "/devices", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> addDevice(@Valid @RequestBody Device device) {

        repository.insert(device);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{mac}")
                .buildAndExpand(device.getAddress())
                .toUri();
        return ResponseEntity.created(location).build();
    }

}
