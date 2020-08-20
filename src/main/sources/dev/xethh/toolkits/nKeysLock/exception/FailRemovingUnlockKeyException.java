package dev.xethh.toolkits.nKeysLock.exception;

import dev.xethh.toolkits.nKeysLock.UnlockKey;

public class FailRemovingUnlockKeyException extends NKeysLockException {
    public FailRemovingUnlockKeyException(UnlockKey unlockKey) {
        super(String.format("Fail to remove lock: Lock[%s] removal return false", unlockKey));
    }
    public FailRemovingUnlockKeyException(UnlockKey unlockKey, Throwable throwable) {
        super(String.format("Fail to remove lock: Lock[%s] removal return false", unlockKey));
    }
}
