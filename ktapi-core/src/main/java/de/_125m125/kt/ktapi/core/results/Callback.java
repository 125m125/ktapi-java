/**
 * The MIT License
 * Copyright © 2017 Kadcontrade
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de._125m125.kt.ktapi.core.results;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Callback<T> {
    public static <T> Callback<T> ofCurried(final Optional<Function<Integer, Consumer<T>>> success,
            final Optional<Function<Integer, Function<String, Consumer<String>>>> failure,
            final Optional<Consumer<Throwable>> error) {
        Objects.requireNonNull(success);
        Objects.requireNonNull(failure);
        Objects.requireNonNull(error);
        return of(success.map(f -> (s, t) -> f.apply(s).accept(t)),
                failure.map(f -> (i, m, h) -> f.apply(i).apply(m).accept(h)), error);
    }

    public static <T> Callback<T> of(final Optional<BiConsumer<Integer, T>> success,
            final Optional<TriConsumer<Integer, String, String>> failure,
            final Optional<Consumer<Throwable>> error) {
        Objects.requireNonNull(success);
        Objects.requireNonNull(failure);
        Objects.requireNonNull(error);
        return new Callback<T>() {

            @Override
            public void onSuccess(final int status, final T result) {
                success.ifPresent(s -> s.accept(status, result));
            }

            @Override
            public void onFailure(final int status, final String message,
                    final String humanReadableMessage) {
                failure.ifPresent(f -> f.accept(status, message, humanReadableMessage));
            }

            @Override
            public void onError(final Throwable t) {
                error.ifPresent(e -> e.accept(t));
            }
        };
    }

    public static <T> Callback<T> successCallback(final Function<Integer, Consumer<T>> success) {
        return ofCurried(Optional.of(success), Optional.empty(), Optional.empty());
    }

    public static <T> Callback<T> successCallback(final BiConsumer<Integer, T> success) {
        return of(Optional.of(success), Optional.empty(), Optional.empty());
    }

    public static <T> Callback<T> failureCallback(
            final Function<Integer, Function<String, Consumer<String>>> failure) {
        return ofCurried(Optional.empty(), Optional.of(failure), Optional.empty());
    }

    public static <T> Callback<T> failureCallback(
            final TriConsumer<Integer, String, String> failure) {
        return of(Optional.empty(), Optional.of(failure), Optional.empty());
    }

    public static <T> Callback<T> errorCallback(final Consumer<Throwable> error) {
        return of(Optional.empty(), Optional.empty(), Optional.of(error));
    }

    public void onSuccess(int status, T result);

    public void onFailure(int status, String message, String humanReadableMessage);

    public void onError(Throwable t);

    /**
     * <p>
     * Represents an operation that accepts three input arguments and returns no
     * result. This is the three-arity specialization of {@link Consumer}.
     * Unlike most other functional interfaces, {@code TriConsumer} is expected
     * to operate via side-effects.
     * </p>
     *
     * <p>
     * This is a <a href="package-summary.html">functional interface</a>
     * whose functional method is {@link #accept(Object, Object, Object)}.
     * </p>
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
        default TriConsumer<T, U, V> andThen(
                final TriConsumer<? super T, ? super U, ? super V> after) {
            Objects.requireNonNull(after);
            return (t, u, v) -> {
                accept(t, u, v);
                after.accept(t, u, v);
            };
        }
    }

}