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

    public boolean equals(Hash other) {
        return value.equals(other.value);
    }
}
