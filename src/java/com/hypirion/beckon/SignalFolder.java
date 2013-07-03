package com.hyprion.beckon;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.List;

import java.util.concurrent.Callable;

public class SignalFolder implements SignalHandler {
    final private Callable[] fns;

    public SignalFolder(List funs) {
        fns = new Callable[funs.size()];
        for (int i = 0; i < fns.length; i++) {
            fns[i] = (Callable) funs.get(i);
        }
    }

    public void handle(Signal sig) {
        for (Callable c : fns) {
            boolean res = false;
            try {
                res = (Boolean) c.call(); // No input elems?
            }
            catch (Exception e) {}
            finally {
                if (!res) {
                    break;
                }
            }
        }
    }
}
