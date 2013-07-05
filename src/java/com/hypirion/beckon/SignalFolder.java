package com.hypirion.beckon;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.List;

import java.util.concurrent.Callable;

public class SignalFolder implements SignalHandler {
    final List originalList;
    final private Callable[] fns;

    public SignalFolder(List funs) {
        fns = new Callable[funs.size()];
        for (int i = 0; i < fns.length; i++) {
            fns[i] = (Callable) funs.get(i);
        }
        originalList = funs;
    }

    public void handle(Signal sig) {
        for (Callable c : fns) {
            boolean cont = true;
            try {
                Object oRes = c.call();
                if (oRes == false || oRes == null) {
                    cont = false;
                }
            }
            catch (Exception e) {}
            finally {
                if (!cont) {
                    break;
                }
            }
        }
    }
}
