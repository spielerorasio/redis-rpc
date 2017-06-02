package com.orasio.redisrpc.exception;

/**
 * Created by spielerl on 26/05/2017.
 */
public class RedisRPCException extends RuntimeException {
    public RedisRPCException() {
    }

    public RedisRPCException(String message) {
        super(message);
    }

    public RedisRPCException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedisRPCException(Throwable cause) {
        super(cause);
    }
}
