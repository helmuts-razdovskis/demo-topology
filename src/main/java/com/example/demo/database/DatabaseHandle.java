package com.example.demo.database;

import com.example.demo.database.bindings.DeviceBinding;
import com.example.demo.database.bindings.DeviceData;
import com.sleepycat.bind.tuple.ByteBinding;
import com.sleepycat.je.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DatabaseHandle {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseHandle.class);

    public final Environment env;
    public final Database db;
    public final SecondaryDatabase indexByType;

    public DatabaseHandle(String envdir, boolean inmemory) {
        logger.info("Setting up database environment (inmem={}) at {}", inmemory, envdir);
        EnvironmentConfig envConfig = new EnvironmentConfig()
                .setAllowCreate(true)
                .setTransactional(true);
        if (inmemory)
            envConfig.setConfigParam(EnvironmentConfig.LOG_MEM_ONLY, "true");

        this.env = new Environment(new File(envdir), envConfig);

        Transaction txn = this.env.beginTransaction(null, null);

        com.sleepycat.je.DatabaseConfig dbConfig = new com.sleepycat.je.DatabaseConfig()
                .setAllowCreate(true)
                .setTransactional(true);

        this.db = env.openDatabase(txn, "TopologyDB", dbConfig);

        SecondaryConfig secConfig = new SecondaryConfig();
        secConfig.setTransactionalVoid(true);
        secConfig.setAllowCreateVoid(true);
        secConfig.setSortedDuplicatesVoid(true);
        secConfig.setAllowPopulateVoid(true);
        secConfig.setKeyCreatorVoid(new SecondaryKeyCreator() {
            private final DeviceBinding binding = new DeviceBinding();

            @Override
            public boolean createSecondaryKey(SecondaryDatabase secondary, DatabaseEntry key, DatabaseEntry data, DatabaseEntry result) {
                DeviceData entry = (DeviceData) binding.entryToObject(data);
                ByteBinding.byteToEntry(entry.getDeviceType(), result);
                return true;
            }
        });
        this.indexByType = env.openSecondaryDatabase(txn, "TopologyByTypeDB", db, secConfig);
        txn.commit();

    }


}
