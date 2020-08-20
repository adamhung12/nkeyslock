package dev.xethh.toolkits.nKeysLock.exception;

public class NKeysLockException extends RuntimeException {
    public NKeysLockException(String message) {
        super(message);
    }

    public NKeysLockException(String message, Throwable cause) {
        super(message, cause);
    }
}
