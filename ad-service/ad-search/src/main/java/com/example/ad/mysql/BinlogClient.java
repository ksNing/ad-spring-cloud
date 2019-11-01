package com.example.ad.mysql;

import com.example.ad.mysql.listener.AggregationListener;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class BinlogClient {
    private BinaryLogClient client;

    private AggregationListener aggregationListener;

    private BinlogConfig binlogConfig;

    @Autowired
    public BinlogClient(BinlogConfig binlogConfig, AggregationListener aggregationListener) {
        this.binlogConfig = binlogConfig;
        this.aggregationListener = aggregationListener;
    }

    public void connect() {
        new Thread(() -> {
            client = new BinaryLogClient(
                    binlogConfig.getHost(),
                    binlogConfig.getPort(),
                    binlogConfig.getUsername(),
                    binlogConfig.getPassword()
            );
            if(!StringUtils.isEmpty(binlogConfig.getBinlogName())
                    && !binlogConfig.getPosition().equals(-1L)) {
                client.setBinlogPosition(binlogConfig.getPosition());
                client.setBinlogFilename(binlogConfig.getBinlogName());
            }

            client.registerEventListener(aggregationListener);

            try {
                log.info("connecting to mysql start");
                client.connect();
                log.info("connecting to mysql done");
            } catch(IOException e) {
                System.out.println("...............");
                e.printStackTrace();
            }
        }).start();
    }

    public void close() {
        try {
            client.disconnect();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
