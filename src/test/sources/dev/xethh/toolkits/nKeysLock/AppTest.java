package dev.xethh.toolkits.nKeysLock;

import dev.xethh.toolkits.nKeysLock.exception.WrongUnlockKeyException;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    @Test
    public void testFlagLocker() {
        NKeysLock locker = NKeysLock.get();
        assertEquals(false, locker.isLocked());
        UnlockKey unlockKey = locker.lock();
        assertEquals(true, locker.isLocked());
        locker.returnKeySilently(unlockKey);
        assertEquals(false, locker.isLocked());
    }

    @Test
    public void testLargeVolume() {
        NKeysLock locker = NKeysLock.get();
        assertEquals(false, locker.isLocked());
        List<UnlockKey> unlockKeys = IntStream.range(0, 5000).boxed().map(it -> locker.lock()).collect(Collectors.toList());
        assertEquals(true, locker.isLocked());
        unlockKeys.stream().forEach(locker::returnKeySilently);
        assertEquals(false, locker.isLocked());
    }

    @Test
    public void testMultipleUpdates() {
        NKeysLock locker = NKeysLock.get();
        assertEquals(false, locker.isLocked());
        UnlockKey unlockKey1 = locker.lock("lock1");
        int i = 100;
        UnlockKey unlockKey2 = locker.lock("lock2");
        UnlockKey unlockKey3 = locker.lock("lock3");
        while (i-- > 0) {
            locker.returnKeySilently(unlockKey2);
            locker.returnKeySilently(unlockKey3);
            unlockKey2 = locker.lock("lock2");
            unlockKey3 = locker.lock("lock2");
        }
        assertEquals(true, locker.isLocked());

    }

    /**
     * Apply lock and unlock operation on the same NKeysLock with multiple threads.
     * The lock should finally unlock
     */
    @Test
    public void testMultipleThreadOperation() {
        NKeysLock lock = NKeysLock.get();
        Observable<UnlockKey> o1 = Observable
                .interval(131, TimeUnit.MILLISECONDS)
                .take(100)
                .observeOn(Schedulers.computation())
                .map(it -> {
                    // System.out.println(String.format("O1: %05d", it));
                    UnlockKey unlockKey = lock.lock("Lock 1");
                    assertEquals(true, lock.isLocked());
                    return unlockKey;
                });
        Observable<UnlockKey> o2 = Observable
                .interval(131, TimeUnit.MILLISECONDS)
                .take(100)
                .observeOn(Schedulers.computation())
                .map(it -> {
                    // System.out.println(String.format("O2: %05d", it));
                    UnlockKey unlockKey = lock.lock("Lock 2");
                    assertEquals(true, lock.isLocked());
                    return unlockKey;
                });
        Observable<UnlockKey> o3 = Observable
                .interval(79, TimeUnit.MILLISECONDS)
                .take(100)
                .observeOn(Schedulers.io())
                .map(it -> {
                    // System.out.println(String.format("O3: %05d", it));
                    UnlockKey unlockKey = lock.lock("Lock 3");
                    assertEquals(true, lock.isLocked());
                    return unlockKey;
                });

        Observable.merge(o1, o2, o3)
                .map(it -> {
                    assertEquals(true, lock.isLocked());
                    lock.returnKeySilently(it);
                    return it;
                })
                .doOnComplete(() -> {
                    assertEquals(false, lock.isLocked());
                })
                .blockingSubscribe()

        ;


    }

    @Test
    public void testForException() {
        NKeysLock locker = NKeysLock.get();
        assertEquals(false, locker.isLocked());

        UnlockKey unlockKey = locker.lock();
        locker.returnKey(unlockKey);
        try {
            locker.returnKey(unlockKey);
        }
        catch (RuntimeException ex){
            if(!(ex instanceof WrongUnlockKeyException)){
                throw ex;
            }
        }

        assertEquals(false, locker.isLocked());
    }

    @Test
    public void testClearAll() {
        NKeysLock locker = NKeysLock.get();
        assertEquals(false, locker.isLocked());
        locker.lock();
        locker.lock();
        locker.lock();
        assertEquals(true, locker.isLocked());
        locker.breakThisLock();
        assertEquals(false, locker.isLocked());
    }

    @Test
    public void testListener(){
        //Init locker
        NKeysLock locker = NKeysLock.get();

        AtomicInteger i = new AtomicInteger();
        //Add listener
        LockStatusListener listener = lockStatus -> i.addAndGet(1);
        locker.addListener(listener);

        //Lock the locker, listener triggered
        UnlockKey unlockKey = locker.lock();
        assertEquals(1, i.get());

        //Another lock, no effect to locker
        locker.lock();
        assertEquals(1, i.get());

        //Clear all lock, i listener triggered again
        locker.breakThisLock();
        assertEquals(2, i.get());

        //Second time clear, no effect to the state
        locker.breakThisLock();
        assertEquals(2, i.get());

        //Locker locked again, listener triggered
        locker.lock();
        assertEquals(3, i.get());

        //Removing lock listener, i should no longer be updated
        locker.removeListener(listener);
        locker.breakThisLock();
        assertEquals(3, i.get());

        //Init j and k for the rest of test
        AtomicInteger j = new AtomicInteger();
        AtomicInteger k = new AtomicInteger();

        //Add 3 listeners
        locker.addListener(lockStatus -> j.addAndGet(2));
        locker.addListener(lockStatus -> j.addAndGet(3));
        locker.addListener(lockStatus -> k.addAndGet(10));

        //Locker locked, should trigger all listeners
        unlockKey = locker.lock();

        //Locker unlocked, should trigger all listeners
        locker.returnKey(unlockKey);
        assertEquals(10, j.get());
        assertEquals(20, k.get());

        //Removing all listeners, j and k should never update again
        locker.removeAllListener();
        locker.lock();
        locker.lock();
        locker.lock();
        locker.lock();
        locker.lock();
        locker.lock();
        assertEquals(10, j.get());
        assertEquals(20, k.get());

        //Listeners removed, even lock cleared j and k should never update again
        locker.breakThisLock();
        assertEquals(10, j.get());
        assertEquals(20, k.get());


    }

}
