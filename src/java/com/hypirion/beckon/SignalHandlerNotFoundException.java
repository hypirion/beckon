package com.hypirion.beckon;

class SignalHandlerNotFoundException extends Exception {
    public SignalHandlerNotFoundException() {
        super("SignalHandler was not found on this JVM -- please report an" +
              "issue at beckon's github page with which JVM and what version" +
              "you're using.");
    }
}
