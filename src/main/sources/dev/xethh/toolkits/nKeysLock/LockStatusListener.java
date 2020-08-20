package dev.xethh.toolkits.nKeysLock;

import java.util.function.Consumer;

/**
 * Listener interface for the multiple lock status change
 */
interface LockStatusListener extends Consumer<LockStatus> {
}
