package org.goodnewstbot.exceptions;

import java.io.IOException;

public class RSSException extends IOException {

    public RSSException(String message) {
        System.out.println("RSS exception for " + message);
    }
}
