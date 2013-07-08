package com.hypirion.beckon;

import java.util.Map;
import java.util.HashMap;

import clojure.lang.Atom;
import clojure.lang.PersistentHashMap;
import clojure.lang.Keyword;

public class SignalAtoms {
    private static final Map<String, Atom> atoms = new HashMap<String, Atom>();
    public static final Keyword SIGNAL = Keyword.intern("signal");

    static final synchronized Atom getSignalAtom(String signame) {
        if (!atoms.containsKey(signame)) {
            Object list = SignalRegistererHelper.getHandlerList(signame);
            PersistentHashMap metadata = PersistentHashMap.create(SIGNAL, signame);
            Atom atm = new Atom(list, metadata);
            atoms.put(signame, atm);
        }
        return atoms.get(signame);
    }
}
