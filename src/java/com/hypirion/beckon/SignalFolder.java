package com.hypirion.beckon;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.List;

import clojure.lang.Seqable;
import clojure.lang.ISeq;

public class SignalFolder implements SignalHandler {
    final Seqable originalList;
    final private Runnable[] fns;

    public SignalFolder(Seqable funs) {
        ISeq seq = funs.seq();
        // seq may be null
        if (seq == null) {
            fns = new Runnable[0];
        }
        else {
            fns = new Runnable[seq.count()];
            for (int i = 0; i < fns.length; i++) {
                fns[i] = (Runnable) seq.first();
                seq = seq.next();
            }
        }
        originalList = funs;
    }

    public void handle(Signal sig) {
        for (Runnable r : fns) {
            boolean cont = true;
            try {
                r.run();
            }
            catch (Exception e) {
                break;
            }
        }
    }
}
