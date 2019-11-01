package com.example.ad.mysql.constant;

import com.github.shyiko.mysql.binlog.event.EventType;

public enum OpType {
    ADD,
    UPDATE,
    DELETE,
    OTHER;

    public static OpType to(EventType eventType) {
        switch(eventType) {
            case UPDATE_ROWS:
                return UPDATE;
            case EXT_DELETE_ROWS:
                return DELETE;
            case EXT_WRITE_ROWS:
                return ADD;
            default:
                return OTHER;
        }
    }

}
