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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ID id = (ID) o;
        return value.equals(id.value);
    }
}
