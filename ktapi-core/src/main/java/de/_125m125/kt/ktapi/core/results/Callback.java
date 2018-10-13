package de._125m125.kt.ktapi.core.results;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Callback<T> {
    public static <T> Callback<T> of(final Optional<Function<Integer, Consumer<T>>> success,
            final Optional<Function<Integer, Function<String, Consumer<String>>>> failure,
            final Optional<Consumer<Throwable>> error) {
        Objects.requireNonNull(success);
        Objects.requireNonNull(failure);
        Objects.requireNonNull(error);
        return new Callback<T>() {

            @Override
            public void onSuccess(final int status, final T result) {
                success.ifPresent(s -> s.apply(status).accept(result));
            }

            @Override
            public void onFailure(final int status, final String message, final String humanReadableMessage) {
                failure.ifPresent(f -> f.apply(status).apply(message).accept(humanReadableMessage));
            }

            @Override
            public void onError(final Throwable t) {
                error.ifPresent(e -> e.accept(t));
            }
        };
    }

    public static <T> Callback<T> successCallback(final Function<Integer, Consumer<T>> success) {
        return of(Optional.of(success), Optional.empty(), Optional.empty());
    }

    public static <T> Callback<T> successCallback(final BiConsumer<Integer, T> success) {
        Objects.requireNonNull(success);
        final Function<Integer, Consumer<T>> curriedSuccess = i -> t -> success.accept(i, t);
        return of(Optional.of(curriedSuccess), Optional.empty(), Optional.empty());
    }

    public static <T> Callback<T> failureCallback(final Function<Integer, Function<String, Consumer<String>>> failure) {
        return of(Optional.empty(), Optional.of(failure), Optional.empty());
    }

    public static <T> Callback<T> failureCallback(final TriConsumer<Integer, String, String> failure) {
        Objects.requireNonNull(failure);
        final Function<Integer, Function<String, Consumer<String>>> curriedFailure = i -> m -> h -> failure.accept(i, m,
                h);
        return of(Optional.empty(), Optional.of(curriedFailure), Optional.empty());
    }

    public static <T> Callback<T> errorCallback(final Consumer<Throwable> error) {
        return of(Optional.empty(), Optional.empty(), Optional.of(error));
    }

    public void onSuccess(int status, T result);

    public void onFailure(int status, String message, String humanReadableMessage);

    public void onError(Throwable t);

    /**
     * Represents an operation that accepts three input arguments and returns no
     * result. This is the three-arity specialization of {@link Consumer}.
     * Unlike most other functional interfaces, {@code TriConsumer} is expected
     * to operate via side-effects.
     *
     * <p>
     * This is a <a href="package-summary.html">functional interface</a>
     * whose functional method is {@link #accept(Object, Object, Object)}.
     *
     * @param <T>
     *            the type of the first argument to the operation
     * @param <U>
     *            the type of the second argument to the operation
     * @param <V>
     *            the type of the third argument to the operation
     *
     * @see Consumer
     */
    @FunctionalInterface
    public static interface TriConsumer<T, U, V> {
        /**
         * Performs this operation on the given arguments.
         *
         * @param t
         *            the first input argument
         * @param u
         *            the second input argument
         * @param v
         *            the third input argument
         */
        void accept(T t, U u, V v);

        /**
         * Returns a composed {@code TriConsumer} that performs, in sequence,
         * this
         * operation followed by the {@code after} operation. If performing
         * either
         * operation throws an exception, it is relayed to the caller of the
         * composed operation. If performing this operation throws an exception,
         * the {@code after} operation will not be performed.
         *
         * @param after
         *            the operation to perform after this operation
         * @return a composed {@code TriConsumer} that performs in sequence this
         *         operation followed by the {@code after} operation
         * @throws NullPointerException
         *             if {@code after} is null
         */
        default TriConsumer<T, U, V> andThen(final TriConsumer<? super T, ? super U, ? super V> after) {
            Objects.requireNonNull(after);

            return (t, u, v) -> {
                accept(t, u, v);
                after.accept(t, u, v);
            };
        }
    }

}