package com.pos.branch.exception;

public class DuplicateSaleException extends RuntimeException {
    public DuplicateSaleException(String message) {
        super(message);
    }
}
