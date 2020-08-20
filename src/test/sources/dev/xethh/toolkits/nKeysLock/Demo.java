package dev.xethh.toolkits.nKeysLock;

public class Demo {
    public static void main(String[] args){
        //Create lock object
        NKeysLock lock = NKeysLock.get();

        // Lock with single key
        System.out.println("Lock with single key");
        System.out.println(lock.isLocked()); //** false
        UnlockKey unlockey = lock.lock();
        System.out.println(lock.isLocked()); //** true
        lock.returnKey(unlockey);
        System.out.println(lock.isLocked()); //** false

        // Multiple unlock key
        System.out.println();
        System.out.println("Multiple unlock key");
        System.out.println(lock.isLocked()); //** false
        UnlockKey unlockey1 = lock.lock();
        System.out.println(lock.isLocked()); //** true
        UnlockKey unlockey2 = lock.lock();
        System.out.println(lock.isLocked()); //** true
        lock.returnKey(unlockey1);
        System.out.println(lock.isLocked()); //** true
        lock.returnKey(unlockey2);
        System.out.println(lock.isLocked()); //** false

        // Lock with listener
        System.out.println();
        System.out.println("Lock with listener");
        lock.addListener(status->{
            switch (status){
                case Unlocked:
                    System.out.println("Lock is unlocked");
                    break;
                case Locked:
                    System.out.println("Lock is locked");
                    break;
            }
        });

        System.out.println(lock.isLocked()); //** false
        UnlockKey unlockey3 = lock.lock();   //** Lock is locked
        System.out.println(lock.isLocked()); //** true
        UnlockKey unlockey4 = lock.lock();
        System.out.println(lock.isLocked()); //** true
        lock.returnKey(unlockey3);
        System.out.println(lock.isLocked()); //** true
        lock.returnKey(unlockey4);           //** Lock is unlocked
        System.out.println(lock.isLocked()); //** false

        // Clear linteners
        lock.removeAllListener();


        // force break the lock
        System.out.println();
        System.out.println("force break the lock");
        System.out.println(lock.isLocked()); //** false
        UnlockKey unlockey5 = lock.lock();
        System.out.println(lock.isLocked()); //** true
        UnlockKey unlockey6 = lock.lock();
        System.out.println(lock.isLocked()); //** true
        lock.breakThisLock();
        System.out.println(lock.isLocked()); //** false



    }
}
