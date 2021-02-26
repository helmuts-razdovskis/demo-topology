package com.example.demo.controllers;

import com.example.demo.database.DatabaseHandle;
import com.example.demo.model.Device;
import com.example.demo.model.DeviceType;
import com.example.demo.model.MacAddress;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldNotFindDevice() {
        ResponseEntity<Device> r_get = restTemplate.getForEntity("http://localhost:" + port + "/devices/FEFEFEFEFEFE", Device.class);
        assertTrue(
                r_get.getStatusCode() == HttpStatus.NOT_FOUND
        );
    }

    @Test
    public void shouldCreateAndGet()
    {
        ResponseEntity r_create = restTemplate.postForEntity("http://localhost:" + port + "/devices",
                new Device(DeviceType.ACCESS_POINT, new MacAddress("fef.fef.fef.000")), Device.class);
        assertTrue(
                r_create.getStatusCode() == HttpStatus.CREATED
        );
        assertTrue(
                r_create.getHeaders().containsKey("location")
        );
        ResponseEntity<Device> r_get = restTemplate.getForEntity(r_create.getHeaders().getLocation(), Device.class);
        assertTrue(r_get.getStatusCode() == HttpStatus.OK
                && r_get.getBody().getDeviceType() == DeviceType.ACCESS_POINT);

    }

    @Test
    public void shouldNotAllowDuplicateMAC() {
        ResponseEntity r_create = restTemplate.postForEntity("http://localhost:" + port + "/devices",
                new Device(DeviceType.SWITCH, new MacAddress("F00.fef.fef.001")), Device.class);
        assertTrue(r_create.getStatusCode() == HttpStatus.CREATED);
        r_create = restTemplate.postForEntity("http://localhost:" + port + "/devices",
                new Device(DeviceType.GATEWAY, new MacAddress("F00.fef.fef.001")), Device.class);
        assertFalse(r_create.getStatusCode() == HttpStatus.CREATED);

    }

    @Test
    public void shouldCreateChild() {
        ResponseEntity r_create1 = restTemplate.postForEntity("http://localhost:" + port + "/devices",
                new Device(DeviceType.GATEWAY, new MacAddress("001.fef.fef.001")), Device.class);
        assertTrue(r_create1.getStatusCode() == HttpStatus.CREATED);

        ResponseEntity r_create2 = restTemplate.postForEntity("http://localhost:" + port + "/devices",
                new Device(DeviceType.ACCESS_POINT, new MacAddress("001.fef.fef.002"), new MacAddress("001.fef.fef.001")), Device.class);
        assertTrue(r_create2.getStatusCode() == HttpStatus.CREATED);

        ResponseEntity r_create3 = restTemplate.postForEntity("http://localhost:" + port + "/devices",
                new Device(DeviceType.ACCESS_POINT, new MacAddress("f01.fef.fef.003"), new MacAddress("001.fef.fef.001")), Device.class);
        assertTrue(r_create3.getStatusCode() == HttpStatus.CREATED);

        ResponseEntity<Device> r_get = restTemplate.getForEntity("http://localhost:" + port + "/topology/001.fef.fef.001", Device.class);
        assertTrue(r_get.getStatusCode() == HttpStatus.OK);
        assertTrue(r_get.getBody().getChildren() != null && r_get.getBody().getChildren().size() == 2);

        assertTrue(
                r_get.getBody().getChildren().stream().anyMatch(c -> "001feffef002".equals(c.getAddress().toString()))
        );

    }



    @Configuration
    @EnableAutoConfiguration
    @ComponentScan(basePackages = { "com.example.demo.controllers", "com.example.demo.database" })
    public static class MyContext {

        @Bean
        @Primary
        public DatabaseHandle getTestDatabase() {
            return new DatabaseHandle("./data/test", true);
        }
    }
}
