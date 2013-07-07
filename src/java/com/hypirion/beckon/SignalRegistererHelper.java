package com.hypirion.beckon;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import java.util.concurrent.Callable;

import clojure.lang.PersistentList;

public class SignalRegistererHelper {

    /**
     * A set of modified signal handlers.
     */
    private final static Set<String> modifiedHandlers = new HashSet<String>();

    /**
     * Registers the new list of functions to the signal name, and returns the
     * old SignalHandler.
     */
    private static SignalHandler setHandler(String signame, List fns) {
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
        SignalHandler old = setHandler(signame, fns);
        modifiedHandlers.add(signame);
    }

    /**
     * Resets/reinits the signal to be handled by its original signal handler.
     *
     * @param signame the name of the signal to reinit.
     */
    static synchronized void resetDefaultHandler(String signame) {
        SignalHandler original = SignalHandler.SIG_DFL;
        Signal sig = new Signal(signame);
        Signal.handle(sig, original);
        modifiedHandlers.remove(sig);
    }

    /**
     * Resets/reinits all the signals back to their original signal handlers,
     * discarding all possible changes done to them.
     */
    static synchronized void resetAll() {
        // To get around the fact that we cannot remove elements from a set
        // while iterating over it.
        List<String> signames = new ArrayList<String>(modifiedHandlers);
        for (String signame : signames) {
            resetDefaultHandler(signame);
        }
    }

    /**
     * Returns a list of Callables which is used within the SignalFolder
     * handling the Signal, or a PersistentList with a Callable SignalHandler if
     * the SignalHandler is not a SignalFolder.
     *
     * @param signame The name of the Signal.
     *
     * @return A list with the Callables used in the SignalFolder.
     */
    static synchronized List getHandlerList(String signame) {
        Signal sig = new Signal(signame);
        // Urgh, no easy way to get current signal handler.
        // Double-handle to get current one without issues.
        SignalHandler current = Signal.handle(sig, SignalHandler.SIG_DFL);
        Signal.handle(sig, current);
        if (current instanceof SignalFolder) {
            return ((SignalFolder)current).originalList;
        }
        else {
            Callable<Boolean> wrappedHandler = new CallableSignalHandler(sig, current);
            return new PersistentList(wrappedHandler);
        }
    }

    /**
     * A Callable SignalHandler is simply a Callable which wraps a
     * SignalHandler. This is used internally to ensure that people can perform
     * <code>swap!</code> in Clojure programs without worrying that the default
     * SignalHandler will cause issues as it's not Callable by default.
     */
    private static class CallableSignalHandler implements Callable<Boolean> {
        private final Signal sig;
        private final SignalHandler handler;

        /**
         * Returns a Callable which will call <code>handler.handle(sig)</code>
         * whenever called.
         */
        CallableSignalHandler(Signal sig, SignalHandler handler) {
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
        public Boolean call() {
            try {
                handler.handle(sig);
                return true;
            } catch (Exception e) { // Not throwable, will still die on errors.
                return false;
            }
        }
    }
}
