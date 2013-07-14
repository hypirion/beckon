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

    /**
     * The keyword <code>:signal</code>.
     */
    public static final Keyword SIGNAL = Keyword.intern("signal");

    /**
     * A standard Atom validator function which tests whether the new value is a
     * Seqable, where each element in the Seqable implements Callable.
     */
    public static final IFn SIGNAL_ATOM_VALIDATOR = new SignalAtomValidator();

    /**
     * Returns a Clojure Atom containing a Seqable. The Seqable represents a
     * list of functions where the functions are called in order whenever a
     * Signal by the type <code>signame</code> is received by this process.
     *
     * @exception SignalHandlerNotFoundException if this code is unable to
     * detect a SignalHandler and/or a Signal class.
     */
    public static final synchronized Atom getSignalAtom(String signame)
        throws SignalHandlerNotFoundException{
        if (!atoms.containsKey(signame)) {
            try {
                Object list = SignalRegistererHelper.getHandlerSeq(signame);
            }
            catch (LinkageError le) {
                throw new SignalHandlerNotFoundException();
            }
            PersistentHashMap metadata = PersistentHashMap.create(SIGNAL, signame);
            Atom atm = new Atom(list, metadata);
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
                // An empty seqable returns null.
                if (seq == null) {
                    return true;
                }
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
