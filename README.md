# beckon

A Clojure library to handle POSIX signals in JVM applications with style and
grace. Sets up with the dirty parts and let you work with it in a (relatively)
simple fashion.

## Quick-start

Add the following dependency to your `project.clj` file:

```clj
[beckon "0.1.0-SNAPSHOT"]
```

Say you want to grab `SIGINT` and, say, ignore every interruption. That is done
as follows:

```clj
(require 'beckon)

(reset! (beckon/signal-atom "INT") [(fn [])])
```

That's it—it's not harder than that. Of course, you may not believe me, so it's
good that we can confirm whether it's possible by using the `raise!` function:

```clj
(beckon/raise! "INT")
; Nothing happens...
(beckon/raise! "TERM")
; NB: This will terminate your JVM process.
```

If you want to go back to the original setup, use `reinit!`:

```clj
(beckon/reinit! "INT")
; Reinitializes the SIGINT signal handler.
(beckon/raise! "INT")
; NB: This will terminate your JVM process.
```

And well, that's really all you need in order to work with beckon.

## Usage

The core of beckon consist of 4 functions: `signal-atom`, `raise!`, `reinit!`
and `reinit-all!`. In addition, there are 2 utility-functions, `always-true` and
`always-false`.

### `signal-atom`

`signal-atom` is the core piece of this library and (ab)uses atoms to setup
signal handlers for Clojurians. As you'd guess, this returns an atom. The atom
has a validator function attached to it, so only Seqable collections where every
element are Callable are legal values in this atom. All Clojure functions
implements Callable, but only functions which has a zero-argument invokation
will actually work as a callable.

We require the contents of the atom to be a Seqable of Callables because it
makes it possible to add multiple signal handlers to a single signal. The signal
handlers will be conditionally executed based upon whether the previous signal
handler returned a truthy of falsey value. If the handler returned a truthy
value, it will continue to execute the other handler, otherwise it will stop.
For example:

```clj
(reset! (beckon/signal-atom "INT")
        [(fn [] (println "foo") true)
         (fn [] (println "bar") false)
         (fn [] (println "We'll never see this"))])
```

Will only print `foo` and `bar`.

Is this sensible? I'm not sure. This certainly complects the library, but I've
very seldom needed more than a single signal handler. For normal applications,
this functionality shouldn't be too hard to reason around. However, if people
have complaints about this functionality, I would certainly like to hear from
you.

The signal handler is automatically updated whenever the atom is updated, but
not vice versa. So if you use beckon, please don't try and hack in signal
handling through another library or through the native java interface. Trust me,
the complexity will kill you if you do.

If an Exception is thrown by a handler, it will be treated as a falsey value.
Errors will not be caught and will crash the Thread.

### `raise!`

`raise!` is probably the most understandable

### `reinit!` and `reinit-all!`

TODO

### utility fns

TODO - clean up that mess

## How is a signal handled?

TODO

## Pitfalls

TODO, mention:

* infinite sequences
* keywords, symbols and other ifns which silently throw stuff.

## License

Copyright © 2013 Jean Niklas L'orange

Distributed under the Eclipse Public License, the same as Clojure.
