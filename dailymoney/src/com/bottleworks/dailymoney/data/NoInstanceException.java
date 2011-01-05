package com.bottleworks.dailymoney.data;

public class NoInstanceException extends Exception {

    private static final long serialVersionUID = 1L;

    public NoInstanceException() {
        super();
    }

    public NoInstanceException(String detailMessage) {
        super(detailMessage);
    }
}
