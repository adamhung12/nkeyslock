package dev.xethh.toolkits.nKeysLock.exception;

import dev.xethh.toolkits.nKeysLock.UnlockKey;

public class WrongUnlockKeyException extends NKeysLockException {
    public WrongUnlockKeyException(UnlockKey unlockKey) {
        super(String.format("Fail to remove lock: Unlock key[%s] not exists", unlockKey));
    }
}
