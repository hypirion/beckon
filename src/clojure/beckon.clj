(ns beckon
  (:import (com.hypirion.beckon SignalAtoms SignalRegisterer)))

(defn signal-atom
  "Returns the beckon atom of the signal with the name signal-name. The changes
  in the atom returned is reflected back to the signal handling, but NOT vice
  versa.

  A beckon atom is an atom containing a Seqable Clojure collection, where all
  the elements in the Seqable collection must be Callable. Clojure functions are
  by default Callable if they take zero arguments. By default would a beckon
  atom be a Clojure List with the default signal handler wrapped within a
  Callable.

  The signal handling of a (modified) beckon atom is as follows: The first
  element in the sequence is called. If the element returns a truthy value, the
  next element will be called in the same fashion. If the element returns a
  falsey value or throws an Exception, the handling will stop and no more
  elements will be called. A default signal handler will always return true
  unless it throws an Exception. If the signal handler throws an Error, that
  Error will not be caught.

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

(defn always-true
  "Takes a function of no arguments, and returns a function taking no arguments
  which calls f and returns true. f has presumably side effects. Will not handle
  throwables."
  [f]
  (fn [] (f) true))

(defn always-false
  "Takes a function of no arguments, and returns a function taking no arguments
  which calls f and returns false. f has presumably side effects. Will not
  handle throwables."
  [f]
  (fn [] (f) false))
