package com.hypirion.beckon;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.List;

public class SignalRegistererHelper {

    static SignalHandler register(String signame, List fns) {
        Signal sig = new Signal(signame);
        SignalFolder folder = new SignalFolder(fns);
        SignalHandler oldHandler = Signal.handle(sig, folder);
        return oldHandler;
    }
}
