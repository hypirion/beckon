package com.hypirion.beckon;

import java.util.List;

import java.util.concurrent.Callable;

import clojure.lang.IFn;
import clojure.lang.ISeq;

public final class SignalAtom {
    public final String signame;

    public SignalAtom(String signame) {
        this.signame = signame;
    }

    public Object deref() {
        return SignalRegistererHelper.getHandlerList(signame);
    }

    public synchronized Object swap(IFn f) {
        Object val = deref();
        Object newVal = f.invoke(val);
        validate(newVal);
        SignalRegistererHelper.register(signame, (List) newVal);
        return newVal;
    }

    public synchronized Object swap(IFn f, Object arg) {
        Object val = deref();
        Object newVal = f.invoke(val, arg);
        validate(newVal);
        SignalRegistererHelper.register(signame, (List) newVal);
        return newVal;
    }

    public synchronized Object swap(IFn f, Object arg1, Object arg2) {
        Object val = deref();
        Object newVal = f.invoke(val, arg1, arg2);
        validate(newVal);
        SignalRegistererHelper.register(signame, (List) newVal);
        return newVal;
    }

    public synchronized Object swap(IFn f, Object x, Object y, ISeq args) {
        Object val = deref();
        Object newVal = f.invoke(val, x, y, args);
        validate(newVal);
        SignalRegistererHelper.register(signame, (List) newVal);
        return newVal;
    }

    public synchronized Object reset(Object newVal) {
        validate(newVal);
        SignalRegistererHelper.register(signame, (List) newVal);
        return newVal;
    }

    private static boolean validate(Object newVal) {
        if (newVal instanceof List) {
            List lst = (List) newVal;
            for (Object o : lst) {
                if (!(newVal instanceof Callable)) {
                    throw new IllegalStateException("Invalid reference state");
                }
            }
            return true;
        }
        else {
            throw new IllegalStateException("Invalid reference state");
        }
    }
}
