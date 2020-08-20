package dev.xethh.toolkits.nKeysLock;

import dev.xethh.toolkits.nKeysLock.exception.FailRemovingUnlockKeyException;
import dev.xethh.toolkits.nKeysLock.exception.FailToOccurUnlockIDException;
import dev.xethh.toolkits.nKeysLock.exception.WrongUnlockKeyException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NKeysLockImpl implements NKeysLock {
    NKeysLockImpl(){
    }

    /**
     * Set storing all the unlock keys to be remove
     */
    private Set<UnlockKey> locks = new HashSet();

    /**
     * Synchronized operation
     *
     * @param operation operation
     * @param <R> return value
     * @return generic R
     */
    synchronized private <R> R lockOperation(Function<Set<UnlockKey>, R> operation) {
        int size = locks.size();

        // Operation for specific lock
        R r = operation.apply(locks);

        //Unlocked
        if (size > 0 && locks.size() == 0)
            listeners.forEach(listener -> listener.accept(LockStatus.Unlocked));
        //Locked
        else if (size == 0 && locks.size() != 0)
            listeners.forEach(listener -> listener.accept(LockStatus.Locked));

        return r;
    }

    /**
     * Check if the lock locked
     *
     * @return true if the lock locked else false
     */
    @Override
    public boolean isLocked() {
        return lockOperation(set -> set.size() > 0);
    }

    /**
     * Obtain a unlock key
     *
     * @return {@link UnlockKey}
     */
    @Override
    public UnlockKey lock() {
        return lock("");
    }

    @Override
    public List<UnlockKey> retrieveLock(String unlockKeyGroup) {
        return this.locks.stream().filter(it-> it.name.equals(unlockKeyGroup)).collect(Collectors.toList());
    }

    /**
     * Obtain a unlock key
     *
     * @param name name of the unlock key
     * @return {@link UnlockKey}
     */
    @Override
    public UnlockKey lock(String name) {
        return lockOperation(set -> {
            int i = 500;
            while (i-- > 0) {
                String uuid = UUID.randomUUID().toString();
                if (!set.contains(uuid)) {
                    UnlockKey unlockKey = new UnlockKey(name==null || name.length()==0 ? uuid : name, uuid);
                    set.add(unlockKey);
                    return unlockKey;
                }
            }
            throw new FailToOccurUnlockIDException();
        });
    }


    /**
     * Unlock this lock
     */
    @Override
    public NKeysLock breakThisLock() {
        lockOperation(set -> {
            set.clear();
            return null;
        });
        return this;
    }

    /**
     * Listeners list on the lock when lock status change
     */
    List<LockStatusListener> listeners = new ArrayList();

    /**
     * Add listener to the lock
     *
     * @param listener {@link LockStatusListener}
     * @return return self for chaining
     */
    @Override
    public NKeysLock addListener(LockStatusListener listener) {
        listeners.add(listener);
        return this;
    }

    /**
     * Remove listener from the lock
     *
     * @param listener {@link LockStatusListener}
     */
    @Override
    public NKeysLock removeListener(LockStatusListener listener) {
        listeners.remove(listener);
        return this;
    }

    /**
     * Clear all the listener
     */
    @Override
    public NKeysLock removeAllListener() {
        listeners.clear();
        return this;
    }

    /**
     * Unlock the lock without any exception
     *
     * @param unlockKey {@link UnlockKey}
     */
    @Override
    public NKeysLock returnKeySilently(UnlockKey unlockKey) {
        unlock(unlockKey, false);
        return this;
    }

    /**
     * Unlock the lock with chance exception thrown
     *
     * @param unlockKey {@link UnlockKey}
     */
    @Override
    public NKeysLock returnKey(UnlockKey unlockKey) {
        unlock(unlockKey, true);
        return this;
    }

    /**
     * The actual unlock method privately
     *
     * @param unlockKey {@link UnlockKey}
     * @param withError exception thrown if true else catch all exception
     */
    private void unlock(UnlockKey unlockKey, boolean withError) {
        lockOperation(set -> {
            if (!set.contains(unlockKey)) {
                if (withError) {
                    throw new WrongUnlockKeyException(unlockKey);
                }
                return null;
            }
            else {
                try {
                    boolean rs = set.remove(unlockKey);
                    if (!rs && withError)
                        throw new FailRemovingUnlockKeyException(unlockKey);
                    else
                        return null;
                } catch (Exception ex) {
                    if (withError)
                        throw new FailRemovingUnlockKeyException(unlockKey,ex);
                    return null;
                }
            }
        });

    }


    @Override
    public void close() {
        removeAllListener();
        breakThisLock();
    }
}
