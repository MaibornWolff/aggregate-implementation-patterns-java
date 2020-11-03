package domain.shared.value;

public final class PersonName {
    public final String givenName;
    public final String familyName;

    private PersonName(String givenName, String familyName) {
        this.givenName = givenName;
        this.familyName = familyName;
    }

    public static PersonName build(String givenName, String familyName) {
        return new PersonName(givenName, familyName);
    }

    public boolean equals(PersonName other) {
        if (!givenName.equals(other.givenName)) return false;

        return familyName.equals(other.familyName);
    }
}
