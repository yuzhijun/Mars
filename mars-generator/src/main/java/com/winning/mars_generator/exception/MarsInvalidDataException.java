package com.winning.mars_generator.exception;

/**
 * data invalid exception
 * Created by yuzhijun on 2018/3/27.
 */
public class MarsInvalidDataException extends RuntimeException {

    public MarsInvalidDataException() {
    }

    public MarsInvalidDataException(String message) {
        super(message);
    }

    public MarsInvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public MarsInvalidDataException(Throwable cause) {
        super(cause);
    }
}
