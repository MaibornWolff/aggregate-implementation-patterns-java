package domain.shared.value;

public final class EmailAddress {
    public final String value;

    private EmailAddress(String value) {
        this.value = value;
    }

    public static EmailAddress build(String emailAddress) {
        return new EmailAddress(emailAddress);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailAddress that = (EmailAddress) o;
        return value.equals(that.value);
    }
}
