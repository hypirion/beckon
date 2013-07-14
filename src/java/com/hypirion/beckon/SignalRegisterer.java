package com.hypirion.beckon;

import java.util.List;

public class SignalRegisterer {

    /**
     * Resets the default handler of the Signal with the name
     * <code>signame</code> to its original value.
     *
     * @exception SignalHandlerNotFoundException if this code is unable to
     * detect a SignalHandler and/or a Signal class.
     */
    public static void resetDefaultHandler(String signame)
        throws SignalHandlerNotFoundException{
        try {
            SignalRegistererHelper.resetDefaultHandler(signame);
        }
        catch (LinkageError le) {
            throw new SignalHandlerNotFoundException();
        }
    }

    /**
     * Resets all signal handlers to their original value.
     *
     * @exception SignalHandlerNotFoundException if this code is unable to
     * detect a SignalHandler and/or a Signal class.
     */
    public static void resetAllHandlers()
        throws SignalHandlerNotFoundException{
        try {
            SignalRegistererHelper.resetAll();
        }
        catch (LinkageError le) {
            throw new SignalHandlerNotFoundException();
        }
    }

    /**
     * Raises a Signal with the name <code>signame</code> in the current
     * process. Will consequently call the current SignalHandler for
     * <code>signame</code> in another Thread with maximal priority.
     *
     * @exception SignalHandlerNotFoundException if this code is unable to
     * detect a SignalHandler and/or a Signal class.
     */
    public static void raiseSignal(String signame)
        throws SignalHandlerNotFoundException {
        try {
            SignalRegistererHelper.raise(signame);
        }
        catch (LinkageError le) {
            throw new SignalHandlerNotFoundException();
        }
    }
}
