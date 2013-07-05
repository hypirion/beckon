package com.hypirion.beckon;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class SignalRegistererHelper {

    /**
     * A set of modified signal handlers.
     */
    private final static Set<String> modifiedHandlers = new HashSet<String>();

    /**
     * Registers the new list of functions to the signal name, and returns the
     * old SignalHandler.
     */
    private static SignalHandler reset_BANG_(String signame, List fns) {
        Signal sig = new Signal(signame);
        SignalFolder folder = new SignalFolder(fns);
        SignalHandler oldHandler = Signal.handle(sig, folder);
        return oldHandler;
    }

    /**
     * Registers the signal name to a List of Callables, where each callable
     * returns an Object. The signal handling is performed as follows: The first
     * callable is called, and if it returns a value equal to <code>false</code>
     * or <code>null</code> it will stop. Otherwise it will repeat on the next
     * callable, until there are no more left.
     *
     * @param signame the signal name to register this list of callables on.
     * @param fns the list of Callables to (potentially) call.
     */
    static synchronized void register(String signame, List fns) {
        SignalHandler old = reset_BANG_(signame, fns);
        modifiedHandlers.add(signame);
    }

    /**
     * Resets/reinits the signal back to its original signal handler, discarding
     * all possible changes.
     *
     * @param signame the name of the signal to reinit.
     */
    static synchronized void reinit_signal_handler_BANG_(String signame) {
        SignalHandler original = SignalHandler.SIG_DFL;
        Signal sig = new Signal(signame);
        Signal.handle(sig, original);
        modifiedHandlers.remove(sig);
    }

    /**
     * Resets/reinits all the signals back to their original signal handlers,
     * discarding all possible changes done to them.
     */
    static synchronized void reinit_all_BANG_() {
        // To get around the fact that we cannot remove elements from a set
        // while iterating over it.
        List<String> signames = new ArrayList(modifiedHandlers);
        for (String signame : signames) {
            reinit_signal_handler_BANG_(signame);
        }
    }
}
