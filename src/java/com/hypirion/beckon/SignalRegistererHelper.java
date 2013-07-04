package com.hypirion.beckon;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SignalRegistererHelper {

    private static Map<String, SignalHandler> originalHandlers =
        new HashMap<String, SignalHandler>();

    /**
     * Registers the new list of functions to the signal name, and returns the
     * old SignalHandler.
     */
    private static SignalHandler reset_BANG_(String signame, List fns) {
        Signal sig = new Signal(signame);
        SignalFolder folder = new SignalFolder(fns);
        SignalHandler oldHandler = Signal.handle(sig, folder);
        return oldHandler;
    }

    static synchronized void register(String signame, List fns) {
        SignalHandler old = reset_BANG_(signame, fns);
        if (!originalHandlers.containsKey(signame)) {
            originalHandlers.put(signame, old);
        }
    }
}
