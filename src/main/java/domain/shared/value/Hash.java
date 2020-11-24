package domain.shared.value;

import java.util.UUID;

public final class Hash {
    public final String value;

    private Hash(String value) {
        this.value = value;
    }

    public static Hash generate() {
        return new Hash(UUID.randomUUID().toString());
    }

    public static Hash build(String hash) {
        return new Hash(hash);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hash hash = (Hash) o;
        return value.equals(hash.value);
    }
}
