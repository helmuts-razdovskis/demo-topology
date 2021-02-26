package com.example.demo.database;

import com.example.demo.database.bindings.DeviceBinding;
import com.example.demo.database.bindings.DeviceData;
import com.example.demo.model.Device;
import com.example.demo.model.DeviceType;
import com.example.demo.model.MacAddress;
import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Repository
public class DemoRepositoryImpl implements DemoRepository {

    private final static Logger logger = LoggerFactory.getLogger(DemoRepositoryImpl.class);

    @Autowired
    private DatabaseHandle handle;


    //TODO possible optimizations with DatabaseEntry pooling (stormpot.Pool, for ex.)

    private static final TupleBinding binding = new DeviceBinding();

    private Device find(long address, boolean fetchChildren, DeviceData prefetched) {
        final DatabaseEntry key = new DatabaseEntry();
        final DatabaseEntry data = new DatabaseEntry();
        logger.info("find({})", Long.toHexString(address));
        LongBinding.longToEntry(address, key);
        if (prefetched != null || handle.db.get(null, key, data, LockMode.READ_COMMITTED) == OperationStatus.SUCCESS) {
            DeviceData dao = prefetched == null ? (DeviceData) binding.entryToObject(data) : prefetched;
            Device device = new Device(
                    DeviceType.values()[dao.getDeviceType()],
                    new MacAddress(dao.getAddress()));
            if (dao.getUplink() != DeviceData.NULL_MAC)
                device.setUplink(new MacAddress(dao.getUplink()));
            if (fetchChildren && !dao.getChildren().isEmpty()) {
                List<Device> list = Collections.synchronizedList(new ArrayList<>());
                dao.getChildren().parallelStream().forEach(A -> {
                    Device ch = find(A, true, null);
                    if (ch != null)
                        list.add(ch);
                });
                if (!list.isEmpty())
                    device.setChildren(list);
            }
            return device;
        }
        return null;
    }

    @Override
    public Device find(MacAddress address) {
        return find(address.getValue(), false, null);
    }

    @Override
    public List<Device> findAll() {
        logger.info("findAll()");
        List<Device> result = new ArrayList<>();

        final DatabaseEntry key = new DatabaseEntry();
        final DatabaseEntry data = new DatabaseEntry();
        try (final SecondaryCursor cursor = handle.indexByType.openCursor(null, new CursorConfig().setReadCommitted(true))) {
            while (cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                DeviceData dao = (DeviceData) binding.entryToObject(data);
                Device device = new Device(
                        DeviceType.values()[dao.getDeviceType()],
                        new MacAddress(dao.getAddress()));
                if (dao.getUplink() != DeviceData.NULL_MAC)
                    device.setUplink(new MacAddress(dao.getUplink()));
                result.add(device);
            }
        }
        return result;
    }

    @Override
    public List<Device> findChildren() {
        logger.info("findChildren()");
        List<Device> result = new ArrayList<>();

        final DatabaseEntry key = new DatabaseEntry();
        final DatabaseEntry data = new DatabaseEntry();
        try (final Cursor cursor = handle.db.openCursor(null, new CursorConfig().setReadCommitted(true))) {
            while (cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                DeviceData dao = (DeviceData) binding.entryToObject(data);
                if (dao.getUplink() == DeviceData.NULL_MAC) {
                    result.add(find(0, true, dao));
                }
            }
        }
        return result;
    }

    @Override
    public Device findChildren(MacAddress start) {
        logger.info("findChildren({})", start);
        return find(start.getValue(), true, null);
    }


    @Override
    public void insert(Device entry) {
        logger.info("insert(type: {}, address: {}, uplink: {})", entry.getDeviceType(), entry.getAddress(), entry.getUplink());
        final DatabaseEntry key = new DatabaseEntry();
        final DatabaseEntry data = new DatabaseEntry();

        Transaction txn = handle.env.beginTransaction(null, new TransactionConfig().setReadCommitted(true));
        try {
            // append child to uplink/parent
            if (entry.getUplink() != null) {
                LongBinding.longToEntry(entry.getUplink().getValue(), key);
                if (handle.db.get(null, key, data, LockMode.READ_COMMITTED) != OperationStatus.SUCCESS) {
                    throw new IllegalArgumentException(String.format("Uplink %s not found for device %s", entry.getUplink(), entry.getAddress()));
                }
                DeviceData dao = (DeviceData) binding.entryToObject(data);
                dao.getChildren().add(entry.getAddress().getValue());

                binding.objectToEntry(dao, data);
                handle.db.put(txn, key, data);
            }

            DeviceData dao = new DeviceData();
            dao.setAddress(entry.getAddress().getValue());
            dao.setDeviceType((byte)entry.getDeviceType().ordinal());
            dao.setUplink(entry.getUplink() == null ? DeviceData.NULL_MAC : entry.getUplink().getValue());
            binding.objectToEntry(dao, data);
            LongBinding.longToEntry(entry.getAddress().getValue(), key);
            if (handle.db.put(txn, key, data, Put.NO_OVERWRITE, null) == null) {
                throw new IllegalArgumentException(String.format("Duplicate device %s", entry.getAddress()));
            }

            txn.commit();
        } catch (Exception e) {
            txn.abort();
            throw e;
        }
    }

}
