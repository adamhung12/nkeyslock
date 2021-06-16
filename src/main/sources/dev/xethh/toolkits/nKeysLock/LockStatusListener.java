package dev.xethh.toolkits.nKeysLock;

import java.util.function.Consumer;

/**
 * Listener interface for the multiple lock status change
 */
public interface LockStatusListener extends Consumer<LockStatus> {
}
