package com.example.ad.mysql.listener;

import com.example.ad.mysql.dto.BinlogRowData;

public interface Ilistener {

    void register();

    void onEvent(BinlogRowData binlogRowData);
}
