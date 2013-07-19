# beckon

A Clojure library to handle POSIX signals in JVM applications with style and
grace. Sets up with the dirty parts and let you work with it in a (relatively)
simple fashion.

## Quick-start

Add the following dependency to your `project.clj` file:

```clj
[beckon "0.1.0-SNAPSHOT"]
```

Say you want to grab `SIGINT` and, say, print "Hahah, nothing can stop me!"
whenever someone attempts to interrupt the process. Hit up your Emacs nREPL
through `nrepl-jack-in` or similar methods, then write the following:

```clj
(require 'beckon)

(let [print-function (fn [] (println "Hahah, nothing can stop me!"))]
  (reset! (beckon/signal-atom "INT") #{print-function}))
```

That's it—it's not harder than that. Of course, you may not believe me, so it's
good that we can confirm whether it's possible by using the `raise!` function:

```clj
(beckon/raise! "INT")
; prints nothing
```

You may think I've really lied, but the thing is, this is ran on a newly spawned
thread with maximum priority. As such, it will not show in the nrepl window.
Move over to the `*nrepl-server*` buffer instead, and you'll see the message. In
case you really do not believe me, you could try out to raise the SIGTERM signal
instead:

```clj
(beckon/raise! "TERM")
; NB: This will terminate nREPL.
```

And if you didn't and want to go back to the original setup, use `reinit!`:

```clj
(beckon/reinit! "INT")
; Reinitializes the SIGINT signal handler.
(beckon/raise! "INT")
; NB: This will terminate your JVM process.
```

And well, that's really all you need in order to work with beckon.

## Usage

The core of beckon consist of 4 functions: `signal-atom`, `raise!`, `reinit!`
and `reinit-all!`. `signal-atom` is the only one needed in production systems,
usually. The other functions help out with debugging and resetting signal
handling back to "factory settings"—the initial setup of signal handlers when
the JVM starts up.

### `signal-atom`

`signal-atom` is the core piece of this library and (ab)uses atoms to setup
signal handlers for Clojurians. As you'd guess, this returns an atom. The atom
has a validator function attached to it, so only Seqable collections where every
element are Runnable are legal values in this atom. All Clojure functions
implements Runnable, but only functions which has a zero-argument invokation
will actually work as a Runnable.

Beckon require the contents of the atom to be a Seqable of Runnable because it
makes it possible to add multiple independend signal handlers to a single
signal. The signal handlers will always be executed sequentially, the only
exception is if one of the functions throw an exception or error. If a function
throws an exception, the signal handling will be cancelled (but no exception
will be thrown), and if a function throws an error, the whole signal handling
crashes. You can in theory "abuse" it to get conditional function dispatching.
For example:

```clj
(reset! (beckon/signal-atom "INT")
        [(fn [] (println "foo"))
         (fn [] (println "bar") (throw (Exception.)))
         (fn [] (println "We'll never see this"))])
```

Will only print `foo` and `bar`.

It's not really a good way to do dispatching though, so you're advised to do
this kind of logic within functions whenever possible.

The signal handler is automatically updated whenever the atom is updated, but
not vice versa. So if you use beckon, please don't try and hack in signal
handling through another library or through the native java interface.

### `raise!`

`raise!` is probably the most understandable function in the system. It will
send off a signal of the type given as input. For instance, `(beckon/raise!
"INT")` will act as if a SIGINT signal was sent to the JVM process. It's handy
to check out that your signal handlers work as intended.

### `reinit!` and `reinit-all!`

TODO

## How is a signal handled?

TODO

## Pitfalls

TODO, mention:

* infinite sequences
* keywords, symbols and other ifns which silently throw stuff.

## License

Copyright © 2013 Jean Niklas L'orange

Distributed under the Eclipse Public License, the same as Clojure.
