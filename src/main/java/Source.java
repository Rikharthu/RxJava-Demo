import io.reactivex.*;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import java.util.*;

/**
 * Created by UberV on 4/4/2017.
 */
public class Source {

    public static final boolean SHOW_ASYNC_DEMO = false;

    public static void main(String[] args) {
        log("Main", "Test");

        Flowable.just("Hello world").subscribe(s -> log("Subscriber", s));
        Observable.just("Hello", "World");

        hello("Jack", "Vasja", "Alyona");

        Flowable.just("Hello world!").subscribe(
                createLoggingConsumer("Logging Consumer, onNext()"),
                createLoggingConsumer("Logging Consumer, onError()"),
                () -> log("Action", "onComplete()")
        );

        if (SHOW_ASYNC_DEMO) {
            createCustomObservableAsync().subscribe(createLoggingObserver("Async Observer (1)"));
            delay(6666);
            log("Info", "Subscribing new async Consumer...");
            Disposable subscribtion = createCustomObservableAsync().subscribe(
                    createLoggingConsumer("Async Consumer (onNext)"),
                    createLoggingConsumer("Async Consumer (onComplete"),
                    () -> log("Async Action (onError)", ""));

            // after some time stop observing
            delay(16000);
            if (!subscribtion.isDisposed()) {
                log("Info", "Unsubscribing async Consumer");
                subscribtion.dispose();
            }
        }

        List<String> words = Arrays.asList(
                "the",
                "quick",
                "brown",
                "fox",
                "jumped",
                "over",
                "the",
                "lazy",
                "dogs"
        );

        // will output the whole array
        System.out.println("\njust(words):");
        Observable.just(words).subscribe(System.out::println);

        // to emit each item individually use fromIterable
        System.out.println("\nfromIterable(words):");
        Observable.fromIterable(words).subscribe(System.out::println);

        System.out.println("\nfromArray(words):");
        Observable.fromArray(words).subscribe(System.out::println);

        // Range operator:
        System.out.println("\nRange operator:");
        Observable.range(1, 5).subscribe(System.out::println);

        // Zip demo:
        // zip combines elements of the source stream with the elements of the supplied stream
        // completes when one of the zipped stream completes
        System.out.println("\nZip demo:");
        Observable.fromIterable(words)
                .zipWith(
                        // supplied stream to combine with
                        Observable.range(1, Integer.MAX_VALUE),
                        // a function that combines pairs of the items (original, supplied)
                        (string, count) -> String.format("%2d. %s", count, string)
                )
                .subscribe(System.out::println);

        // Flatmap:
        // list the letters comprising words
        System.out.println("\nFlatMap demo:");
        Observable.fromIterable(words)
                // split the words into array of it's characters
                // and flatmap those to create a new Observable
                // consisting of all the characters of all the words
                .flatMap(word -> Observable.fromArray(word.split("")))
                // emits only distinct elements (distinct/unique characters)
                .distinct()
                // emits items(characters) in sorted order
                .sorted()
                .zipWith(Observable.range(1, Integer.MAX_VALUE),
                        (string, count) -> String.format("%2d. %s", count, string))
                .subscribe(System.out::println);
    }

    public static void log(String tag, String message) {
        System.out.println(String.format("%s :\t%s", tag, message));
    }

    public static void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hello(String... names) {
        Observable.fromArray(names).subscribe(s -> log("Greeting", "Hello, " + s + "!"));
    }

    public static Observer createLoggingObserver(final String tag) {
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                log(tag, "onSubscribe()");
            }

            @Override
            public void onNext(String s) {
                log(tag, "onNext(), " + s);
            }

            @Override
            public void onError(Throwable throwable) {
                log(tag, "onError()" + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                log(tag, "onComplete()");
            }
        };
    }

    public static Consumer createLoggingConsumer(String tag) {
        return o -> log(tag, o.toString());
    }

    public static int[] createIntArray(int n) {
        int array[] = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = i + 1;
        }
        return array;
    }

    public static Observable createCustomObservableBlocking() {
        // create an observable that will emit items
        Observable<String> o = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                try {
                    for (int i = 0; i < 70; i++) {
                        log("(B) Emitter", "Value #" + i);
                        emitter.onNext("Value #" + i);
                        Thread.sleep(10);

                        emitter.onComplete();
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
        return o;
    }

    public static Observable createCustomObservableAsync() {
        Observable<String> o = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> obsEmitter) throws Exception {
                final ObservableEmitter<String> emitter = obsEmitter;
                new Thread(() -> {
                    try {
                        for (int i = 0; i < 20; i++) {
                            // By default, if Disposable.dispose() is called
                            // Observable will continue to emit items
                            // Perform a check if subscriber did not unsubscribe
                            if (obsEmitter.isDisposed()) return;

                            log("(A) Emitter", "Value #" + i);
                            emitter.onNext("Value #" + i);
                            Thread.sleep(1000);
                        }
                        log("(A) Emitter", "onComplete");
                        emitter.onComplete();
                    } catch (Exception e) {
                        log("(A) Emitter", "onError");
                        emitter.onError(e);
                    }
                }).start();
            }
        });
        return o;
    }

}
