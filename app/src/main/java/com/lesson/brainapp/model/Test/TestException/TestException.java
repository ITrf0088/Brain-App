package com.lesson.brainapp.model.Test.TestException;

public class TestException extends Exception {

    public TestException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestException(String message) {
        super(message);
    }
}
