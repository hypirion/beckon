(ns beckon
  (:import (com.hypirion.beckon SignalAtoms SignalRegisterer)))

(defn signal-atom [signal-name]
  (SignalAtoms/getSignalAtom signal-name))

(defn raise! [signal-name]
  (SignalRegisterer/raiseSignal signal-name))

(defn re-init! [signal-name]
  (SignalRegisterer/resetDefaultHandler signal-name))

(defn re-init-all! []
  (SignalRegisterer/resetAllHandlers))

(defn always-true
  "Takes a function of no arguments, and returns a function taking no arguments
  which calls f and returns true. f has presumably side effects."
  [f]
  (fn [] (f) true))

(defn always-false
  "Takes a function of no arguments, and returns a function taking no arguments
  which calls f and returns false. f has presumably side effects."
  [f]
  (fn [] (f) false))
