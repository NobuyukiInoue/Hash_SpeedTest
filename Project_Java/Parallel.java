// Java "Parallel.For" ?
// https://codeday.me/jp/qa/20181210/54383.html

import java.lang.String;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;
import java.util.concurrent.*;
import java.lang.*;


public class Parallel {
    private static final int NUM_CORES = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService forPool = Executors.newFixedThreadPool(NUM_CORES * 2, new NamedThreadFactory("Parallel.For"));
    public static <T> void For(final Iterable<T> elements, final Operation<T> operation) {
        try {
            // invokeAll blocks for us until all submitted tasks in the call complete
            forPool.invokeAll(createCallables(elements, operation));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static <T> Collection<Callable<Void>> createCallables(final Iterable<T> elements, final Operation<T> operation) {
        List<Callable<Void>> callables = new LinkedList<Callable<Void>>();
        for (final T elem : elements) {
            callables.add(new Callable<Void>() {
                @Override
                public Void call() {
                    operation.perform(elem);
                    return null;
                }
            });
        }

        return callables;
    }

    public static interface Operation<T> {
        public void perform(T pParameter);
    }
}
