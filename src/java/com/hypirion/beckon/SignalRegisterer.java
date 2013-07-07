package com.hypirion.beckon;

import java.util.List;

public class SignalRegisterer {
    public static void setHandlerList(String signame, List fns)
        throws SignalHandlerNotFoundException{
        try {
            SignalRegistererHelper.register(signame, fns);
        }
        catch (LinkageError le) {
            throw new SignalHandlerNotFoundException();
        }
    }
}
