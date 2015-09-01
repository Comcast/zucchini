package com.comcast.zucchini;

import java.lang.RuntimeException;

public class ZucchiniRuntimeException extends RuntimeException {
    public ZucchiniRuntimeException(String msg) {
        super(msg);
    }
}
