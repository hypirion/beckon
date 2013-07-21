# beckon changelog

## 0.1.0

* **New:** The function `signal-atom` returns an atom containing a collection of
  functions. The functions will be invoked sequentially whenever a signal is
  trapped.
* **New:** The function `raise!` raises a signal.
* **New:** `reinit!` and `reinit-all!` reinitializes signal handlers, and return
  them to their "factory settings".
