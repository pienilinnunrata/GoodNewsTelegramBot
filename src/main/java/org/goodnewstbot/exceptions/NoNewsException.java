package org.goodnewstbot.exceptions;

public class NoNewsException extends NullPointerException {
    public NoNewsException() {
        System.out.println("No news found");
    }
}
