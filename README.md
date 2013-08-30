# beckon

A Clojure library to handle POSIX signals in JVM applications with style and
grace. Sets up with the dirty parts and let you work with it in a (relatively)
simple fashion.

## Quick-start

Add the following dependency to your `project.clj` file:

```clj
[beckon "0.1.1"]
```

Say you want to grab `SIGINT` and, say, print "Hahah, nothing can stop me!"
whenever someone attempts to interrupt the process. Hit up your Emacs nREPL
through `nrepl-jack-in` or similar methods, then write the following:

```clj
(require 'beckon)

(let [print-function (fn [] (println "Hahah, nothing can stop me!"))]
  (reset! (beckon/signal-atom "INT") #{print-function}))
```

That's it—it's not harder than that. To confirm whether this works or not, we
can use the `raise!` function, which will raise a POSIX signal to the VM:

```clj
(beckon/raise! "INT")
; prints nothing
```

So why didn't `raise!` print out anything? Whenever the JVM receives a signal,
it decides to start up a new thread with maximum priority and do the signal
handling asynchronously. As such, it will not show in the nrepl window. Move
over to the `*nrepl-server*` buffer instead, and you'll see the message.

By default, things like SIGTERM and SIGINT will terminate the running VM, so be
a bit careful. We can of course play around with it in a REPL:

```clj
(beckon/raise! "TERM")
; NB: This will terminate nREPL.
```

And if you somehow managed to screw up the signal handling and want to go back
to the default, that's possible too:

```clj
(beckon/reinit! "INT")
; Reinitializes the SIGINT signal handler.
(beckon/raise! "INT")
; NB: This will terminate your JVM process.
```

And well, that's really all you need to know in order to work with beckon.

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

`reinit!` and `reinit-all!` are functions which reset the signal handlers back
to their original state when the JVM was started. `reinit!` takes a single
argument, the signal to reset, whereas `reinit-all!` takes zero and resets every
single one.

## How is a signal handled?

In the JVM, whenever a signal is received, the VM starts up a new thread at
`Thread.MAX_PRIORITY`, and executes it asynchronously. That's why we don't see
any printing in nREPL, although it should work fine in command-line programs. I
would recommend, however, to have some sort of logger or printer a signal
handler sends a message to, instead of having the signal handler printing
manually.

## "FAQ"

People using this library may get some issues when using it. If this list of
common problems doesn't help you, please add a [new issue][new-issue] and we'll
see what we can do about it!

**Q:** My infinite sequence doesn't work with this library, why is that?  
**A:** This library is designed to be easy to use for Clojure developers,
  without sacrificing speed. As such, the collection of functions are realized
  within Beckon and stored within a Java array. An infinite sequence doesn't fit
  in a Java array, sadly.

**Q:** For some reason, keywords, symbols and other things which are clearly not
  functions are allowed in the collection of functions! Why is that?  
**A:** Keywords, symbols and some persistent collections implement the `IFn`
  interface in Clojure, which automatically means that they do in fact implement
  Runnable. But, as mentioned, while they still implement Runnable, that won't
  mean they are actually able to send back something of value. This is intended
  to be fixed in a later version.

[new-issue]: https://github.com/hyPiRion/beckon/issues/new "Add a new issue to Beckon"

## License

Copyright © 2013 Jean Niklas L'orange

Distributed under the Eclipse Public License, the same as Clojure.
