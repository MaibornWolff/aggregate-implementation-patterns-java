package domain.shared.exception;

public class WrongConfirmationHashException extends Exception {
    public WrongConfirmationHashException() {
        super("confirmation hash does not match");
    }
}
