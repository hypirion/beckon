package com.hypirion.beckon;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import clojure.lang.PersistentHashSet;
import clojure.lang.Seqable;

public class SignalRegistererHelper {

    /**
     * A set of modified signal handlers.
     */
    private final static Map<String, SignalHandler> originalHandlers =
        new HashMap<String, SignalHandler>();

    /**
     * Registers the new list of functions to the signal name, and returns the
     * old SignalHandler.
     */
    private static SignalHandler setHandler(String signame, Seqable fns) {
        Signal sig = new Signal(signame);
        SignalFolder folder = new SignalFolder(fns);
        SignalHandler oldHandler = Signal.handle(sig, folder);
        return oldHandler;
    }

    /**
     * Registers the signal name to a List of Runnables, where each callable
     * returns an Object. The signal handling is performed as follows: The first
     * callable is called, and if it returns a value equal to <code>false</code>
     * or <code>null</code> it will stop. Otherwise it will repeat on the next
     * callable, until there are no more left.
     *
     * @param signame the signal name to register this list of callables on.
     * @param fns the list of Runnables to (potentially) call.
     */
    static synchronized void register(String signame, Seqable fns) {
        SignalHandler old = setHandler(signame, fns);
        if (!originalHandlers.containsKey(signame)) {
            originalHandlers.put(signame, old);
        }
    }

    /**
     * Resets/reinits the signal to be handled by its original signal handler.
     *
     * @param signame the name of the signal to reinit.
     */
    static synchronized void resetDefaultHandler(String signame)
        throws SignalHandlerNotFoundException {
        if (originalHandlers.containsKey(signame)) {
            SignalHandler original = originalHandlers.get(signame);
            Signal sig = new Signal(signame);
            Signal.handle(sig, original);
            originalHandlers.remove(sig);
            SignalAtoms.getSignalAtom(signame).reset(getHandlerSeq(signame));
            // As the Atom has a watch which calls register, the handle has been
            // modified again. Perform another handle call to fix this:
            Signal.handle(sig, original);
        }
    }

    /**
     * Resets/reinits all the signals back to their original signal handlers,
     * discarding all possible changes done to them.
     */
    static synchronized void resetAll() throws SignalHandlerNotFoundException {
        // To get around the fact that we cannot remove elements from a set
        // while iterating over it.
        List<String> signames = new ArrayList<String>(originalHandlers.keySet());
        for (String signame : signames) {
            resetDefaultHandler(signame);
        }
    }

    /**
     * Returns a set of Runnables which is used within the SignalFolder
     * handling the Signal, or a PersistentSet with a Runnable SignalHandler if
     * the SignalHandler is not a SignalFolder.
     *
     * @param signame The name of the Signal.
     *
     * @return A list with the Runnables used in the SignalFolder.
     */
    static synchronized Seqable getHandlerSeq(String signame) {
        Signal sig = new Signal(signame);
        // Urgh, no easy way to get current signal handler.
        // Double-handle to get current one without issues.
        SignalHandler current = Signal.handle(sig, SignalHandler.SIG_DFL);
        Signal.handle(sig, current);
        if (current instanceof SignalFolder) {
            return ((SignalFolder)current).originalList;
        }
        else {
            Runnable wrappedHandler = new RunnableSignalHandler(sig, current);
            return PersistentHashSet.create(wrappedHandler);
        }
    }

    /**
     * A Runnable SignalHandler is simply a Runnable which wraps a
     * SignalHandler. This is used internally to ensure that people can perform
     * <code>swap!</code> in Clojure programs without worrying that the default
     * SignalHandler will cause issues as it's not Runnable by default.
     */
    private static class RunnableSignalHandler implements Runnable {
        private final Signal sig;
        private final SignalHandler handler;

        /**
         * Returns a Runnable which will call <code>handler.handle(sig)</code>
         * whenever called.
         */
        RunnableSignalHandler(Signal sig, SignalHandler handler) {
            this.sig = sig;
            this.handler = handler;
        }

        /**
         * Calls the SignalHandler with the signal provided at construction, and
         * returns true if the handler doesn't cast any exception. If the
         * handler cast an exception, false is returned, and if the handler
         * casts an error, that error is cast.
         *
         * @return true if the handler doesn't throw an exception, false
         * otherwise.
         */
        @Override
        public void run() {
            handler.handle(sig);
        }
    }

    static void raise(String signame) {
        Signal sig = new Signal(signame);
        Signal.raise(sig);
    }
}
