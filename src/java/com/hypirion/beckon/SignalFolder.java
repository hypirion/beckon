package com.hypirion.beckon;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.List;

import java.util.concurrent.Callable;

import clojure.lang.Seqable;
import clojure.lang.ISeq;

public class SignalFolder implements SignalHandler {
    final Seqable originalList;
    final private Callable[] fns;

    public SignalFolder(Seqable funs) {
        ISeq seq = funs.seq();
        // seq may be null
        if (seq == null) {
            fns = new Callable[0];
        }
        else {
            fns = new Callable[seq.count()];
            for (int i = 0; i < fns.length; i++) {
                fns[i] = (Callable) seq.first();
                seq = seq.next();
            }
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
