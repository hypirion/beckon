package com.hyprion.beckon;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.List;

import java.util.concurrent.Callable;

public class SignalFolder {
    final private Callable[] fns;

    public SignalFolder(List funs) {
        fns = new Callable[funs.size()];
        for (int i = 0; i < fns.length; i++) {
            fns[i] = (Callable) funs.get(i);
        }
    }
    
}
