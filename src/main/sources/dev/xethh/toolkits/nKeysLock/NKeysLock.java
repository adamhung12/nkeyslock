package dev.xethh.toolkits.nKeysLock;

import java.util.List;
import java.util.Objects;

/**
 * A N Keys Lock is a lock that can be locked multiple times. Every time the lock locks,
 * a new unlock key released to the caller. The lock is locked when the first time caller calls
 * the {@link #lock()} function and a unlock key is return the caller.
 * And the lock is unlocked in two cases:
 * 1. all the unlock key is returned to the lock by calling {@link #returnKey(UnlockKey)}
 * 2. force breaking the lock by calling {@link #breakThisLock()}
 *
 * The status switch between locked and unlocked. {@link LockStatusListener} can be applied to the
 * N Key Lock, so the operation is called when status change
 */
public interface NKeysLock extends AutoCloseable{

    /**
     * Check if the lock is locked
     *
     * @return true if locked else false
     */
    boolean isLocked();

    /**
     * Check if the lock unlocked
     *
     * @return true if the lock unlocked else false
     */
    default boolean isNotLocked() {
        return !isLocked();
    }

    ;

    /**
     * Lock the N Keys Lock without naming the lock
     *
     * @return {@link UnlockKey}
     */
    UnlockKey lock();

    /**
     * Retrieve the {@link UnlockKey} with a name
     * @param unlockKeyGroup the group name of unlock key
     * @return List of {@link UnlockKey}
     */
    List<UnlockKey> retrieveLock(String unlockKeyGroup);

    /**
     * Lock the N Keys lock with name. In case the key distributed as string.
     *
     * @param unlockKeyName name of the unlock key
     * @return {@link UnlockKey}
     */
    UnlockKey lock(String unlockKeyName);

    /**
     * return the unlock key.
     * if all the unlock is returned, the lock status change to {@link LockStatus#Unlocked}
     *
     * @param unlockKey {@link UnlockKey}
     * @return the object it self({@link NKeysLock})
     */
    NKeysLock returnKey(UnlockKey unlockKey);

    /**
     * return the unlock key silently.
     * In {@link #returnKey(UnlockKey)}, there will be some exception thrown, such as key not exist
     *
     * @param unlockKey {@link UnlockKey}
     * @return the object it self({@link NKeysLock})
     */
    NKeysLock returnKeySilently(UnlockKey unlockKey);

    /**
     * Directly unlock the lock and dispose all the {@link UnlockKey}
     *
     * @return the object it self({@link NKeysLock})
     */
    NKeysLock breakThisLock();


    /**
     * Add {@link LockStatusListener} to the lock. Once {@link LockStatus} change,
     * {@link LockStatusListener} will be triggered
     *
     * @param listener {@link LockStatusListener}
     * @return the object it self({@link NKeysLock})
     */
    NKeysLock addListener(LockStatusListener listener);

    /**
     * Remove a {@link LockStatusListener} from the lock.
     *
     * @param listener {@link LockStatusListener}
     * @return the object it self({@link NKeysLock})
     */
    NKeysLock removeListener(LockStatusListener listener);

    /**
     * Remove all {@link LockStatusListener} from the lock
     *
     * @return the object it self({@link NKeysLock})
     */
    NKeysLock removeAllListener();



    /**
     * Static get method
     *
     * @return new {@link NKeysLock}
     */
    static NKeysLock get() {
        return new NKeysLockImpl();
    }
}
