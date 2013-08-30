# beckon changelog

## 0.1.1 [`docs`][0.1.0-docs] [`tag`][0.1.1-tag]

* Beckon will now always compile to 1.6-compliant bytecode.

## 0.1.0 [`docs`][0.1.0-docs] [`tag`][0.1.0-tag]

* **New:** The function `signal-atom` returns an atom containing a collection of
  functions. The functions will be invoked sequentially whenever a signal is
  trapped.
* **New:** The function `raise!` raises a signal.
* **New:** `reinit!` and `reinit-all!` reinitializes signal handlers, and return
  them to their "factory settings".

[0.1.1-tag]: https://github.com/hyPiRion/beckon/tree/0.1.1
[0.1.0-tag]: https://github.com/hyPiRion/beckon/tree/0.1.0
[0.1.0-docs]: http://hypirion.github.com/beckon/0.1.0/
