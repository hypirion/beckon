package com.hypirion.beckon;

import java.util.List;

public class SignalRegisterer {
    public static void resetDefaultHandler(String signame)
        throws SignalHandlerNotFoundException{
        try {
            SignalRegistererHelper.resetDefaultHandler(signame);
        }
        catch (LinkageError le) {
            throw new SignalHandlerNotFoundException();
        }
    }

    public static void resetAllHandlers()
        throws SignalHandlerNotFoundException{
        try {
            SignalRegistererHelper.resetAll();
        }
        catch (LinkageError le) {
            throw new SignalHandlerNotFoundException();
        }
    }

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
