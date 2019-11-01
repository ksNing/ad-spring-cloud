package com.example.ad.vo.exception;

public class AdException extends Exception {
    private String message;
    public AdException(String message) {
        super(message);
    }
}
