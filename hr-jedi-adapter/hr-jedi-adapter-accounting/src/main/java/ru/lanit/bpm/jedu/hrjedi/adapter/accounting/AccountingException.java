package ru.lanit.bpm.jedu.hrjedi.adapter.accounting;

public class AccountingException extends Exception {
    public AccountingException(String message) {
        super(message);
    }

    public AccountingException(String message, Throwable cause) {
        super(message, cause);
    }
}
