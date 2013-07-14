(ns beckon
  (:import [com.hypirion.beckon SignalAtoms]
           [sun.misc Signal]))

(defn signal-atom [signal-name]
  (SignalAtoms/getSignalAtom signal-name))

(defn raise! [signal-name]
  (Signal/raise (Signal. signal-name)))

(defn true!
  "Takes a function of no arguments, and returns a function taking no arguments
  which calls f and returns true. f has presumably side effects."
  [f]
  (fn [] (f) true))

(defn false!
  "Takes a function of no arguments, and returns a function taking no arguments
  which calls f and returns false. f has presumably side effects."
  [f]
  (fn [] (f) true))
