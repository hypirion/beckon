package com.hypirion.beckon;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.util.concurrent.Callable;

import clojure.lang.Atom;
import clojure.lang.PersistentHashMap;
import clojure.lang.Keyword;
import clojure.lang.AFn;
import clojure.lang.IFn;

public class SignalAtoms {
    private static final Map<String, Atom> atoms = new HashMap<String, Atom>();
    public static final Keyword SIGNAL = Keyword.intern("signal");
    public static final IFn SIGNAL_ATOM_VALIDATOR = new SignalAtomValidator();

    static final synchronized Atom getSignalAtom(String signame) {
        if (!atoms.containsKey(signame)) {
            Object list = SignalRegistererHelper.getHandlerList(signame);
            PersistentHashMap metadata = PersistentHashMap.create(SIGNAL, signame);
            Atom atm = new Atom(list, metadata);
            atm.setValidator(SIGNAL_ATOM_VALIDATOR);
            atoms.put(signame, atm);
        }
        return atoms.get(signame);
    }

    private static class SignalAtomValidator extends AFn {
        @Override
        public Object invoke(Object newVal) {
            if (newVal instanceof List) {
                List lst = (List) newVal;
                for (Object o : lst) {
                    if (!(newVal instanceof Callable)) {
                        return false;
                    }
                }
                return true;
            }
            else {
                return false;
            }
        }
    }
}
