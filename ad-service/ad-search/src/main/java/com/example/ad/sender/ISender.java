package com.example.ad.sender;

import com.example.ad.mysql.dto.MySqlRowData;

public interface ISender {

    void sender(MySqlRowData mySqlRowData);
}
