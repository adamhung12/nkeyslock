package dev.xethh.toolkits.nKeysLock.exception;

import dev.xethh.toolkits.nKeysLock.UnlockKey;

public class FailToOccurUnlockIDException extends NKeysLockException {
    public FailToOccurUnlockIDException() {
        super("Fail to occur new unlock key id");
    }
}
