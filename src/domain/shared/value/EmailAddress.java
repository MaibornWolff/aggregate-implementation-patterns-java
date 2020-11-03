package domain.shared.value;

public final class EmailAddress {
    public final String value;

    private EmailAddress(String value) {
        this.value = value;
    }

    public static EmailAddress build(String emailAddress) {
        return new EmailAddress(emailAddress);
    }

    public boolean equals(EmailAddress other) {
        return value.equals(other.value);
    }
}
