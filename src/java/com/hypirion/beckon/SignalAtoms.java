package com.hypirion.beckon;

import java.util.Map;
import java.util.HashMap;

import java.util.concurrent.Callable;

import clojure.lang.Atom;
import clojure.lang.PersistentHashMap;
import clojure.lang.Keyword;
import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.Seqable;
import clojure.lang.ISeq;

public class SignalAtoms {
    private static final Map<String, Atom> atoms = new HashMap<String, Atom>();
    public static final Keyword SIGNAL = Keyword.intern("signal");
    public static final IFn SIGNAL_ATOM_VALIDATOR = new SignalAtomValidator();

    public static final synchronized Atom getSignalAtom(String signame) {
        if (!atoms.containsKey(signame)) {
            Object list = SignalRegistererHelper.getHandlerSeq(signame);
            PersistentHashMap metadata = PersistentHashMap.create(SIGNAL, signame);
            Atom atm = new Atom(list, metadata);
            System.out.println(list);
            atm.setValidator(SIGNAL_ATOM_VALIDATOR);
            SignalAtomWatch saw = new SignalAtomWatch(signame);
            atm.addWatch(signame, saw);
            atoms.put(signame, atm);
        }
        return atoms.get(signame);
    }

    private static class SignalAtomValidator extends AFn {
        @Override
        public Object invoke(Object newVal) {
            if (newVal instanceof Seqable) {
                ISeq seq = ((Seqable) newVal).seq();
                int count = seq.count();
                while (count --> 0) {
                    Object o = seq.first();
                    seq = seq.next();
                    if (!(o instanceof Callable)) {
                        return false;
                    }
                }
                return true;
            }
            else if (newVal == null) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    private static class SignalAtomWatch extends AFn {
        public final String signame;

        public SignalAtomWatch(String signame) {
            this.signame = signame;
        }

        @Override
        public Object invoke(Object key, Object ref, Object oldState, Object newState) {
            SignalRegistererHelper.register(signame, (Seqable) newState);
            return null;
        }
    }
}
