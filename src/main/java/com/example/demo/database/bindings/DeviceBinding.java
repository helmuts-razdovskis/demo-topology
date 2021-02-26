package com.example.demo.database.bindings;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class DeviceBinding extends TupleBinding {

    private static final Logger logger = LoggerFactory.getLogger(DeviceBinding.class);

    public Object entryToObject(TupleInput ti) {
        DeviceData device = new DeviceData();
        device.setDeviceType(ti.readByte());
        device.setAddress(ti.readLong());
        device.setUplink(ti.readLong());
        try {
            byte[] remain = ti.readAllBytes();
            if (remain.length > 0) {
                long[] ch = new long[remain.length / Long.BYTES];
                ByteBuffer.wrap(remain).asLongBuffer().get(ch);
                Arrays.stream(ch).forEach(a -> device.getChildren().add(a & 0xFFFFFFFFFFFFL));
            }
        } catch (Exception e) {
            logger.warn("entryToObject: {}", e.getMessage());
        }

        return device;
    }

    public void objectToEntry(Object object, TupleOutput to) {
        DeviceData device = (DeviceData) object;
        to.writeByte(device.getDeviceType());
        to.writeLong(device.getAddress());
        to.writeLong(device.getUplink());
        device.getChildren().forEach(a -> to.writeLong(a));
    }
}
