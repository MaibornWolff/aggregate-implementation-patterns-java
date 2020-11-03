package domain.shared.value;

import java.util.UUID;

public final class ID {
    public final String value;

    private ID(String value) {
        this.value = value;
    }

    public static ID generate() {
        return new ID(UUID.randomUUID().toString());
    }

    public static ID build(String id) {
        return new ID(id);
    }

    public boolean equals(ID other) {
        return value.equals(other.value);
    }
}
