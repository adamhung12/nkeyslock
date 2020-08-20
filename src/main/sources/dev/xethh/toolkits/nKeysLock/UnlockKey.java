package dev.xethh.toolkits.nKeysLock;

import java.util.Objects;

/**
 * Unlock key for {@link NKeysLock}
 */
public class UnlockKey {

    String id = null;
    String name = null;

    protected UnlockKey(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnlockKey unlockKey = (UnlockKey) o;
        return Objects.equals(id, unlockKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Lock{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
