(ns beckon
  (:import (com.hypirion.beckon SignalAtoms SignalRegisterer)))

(defn signal-atom
  "Returns the beckon atom of the signal with the name signal-name. The changes
  in the atom returned is reflected back to the signal handling, but NOT vice
  versa. Multiple calls for the same signal atom will return the
  same (identical) atom.

  A beckon atom is an atom containing a Seqable Clojure collection, where all
  the elements in the Seqable collection must be Runnable. Clojure functions are
  by default Runnable if they can take zero arguments. By default would a beckon
  atom be a Clojure Set with the default signal handler wrapped within a
  Runnable. Note that with sets, the ordering of the Runnable is arbitrary; If
  you need ordered calling, convert the set to a vector or list first.

  The signal handling of a (modified) beckon atom is done by simply calling all
  the functions in order. If the Runnable throws an Exception, the handling will
  stop and no more elements will be called. If the signal handler throws an
  Error, that Error will not be caught.

  signal-name must be a legal POSIX signal, where SIG is omitted from the first
  part of the name."
  [signal-name]
  (SignalAtoms/getSignalAtom signal-name))

(defn raise!
  "Raises a signal of the type specified. Consequently, the signal will also be
  handled by the signal handling procedure.

  signal-name must be a legal POSIX signal, where SIG is omitted from the first
  part of the name."
  [signal-name]
  (SignalRegisterer/raiseSignal signal-name))

(defn reinit!
  "Reinitialises the signal handler to the signal name, just as it were at the
  start of this JVM process."
  [signal-name]
  (SignalRegisterer/resetDefaultHandler signal-name))

(defn reinit-all!
  "Reintializes all signal handlers handled by beckon to their default values."
  []
  (SignalRegisterer/resetAllHandlers))
