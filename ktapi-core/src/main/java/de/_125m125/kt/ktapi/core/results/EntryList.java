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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntryList<E> {

    public static <E> EntryList<E> of(final Collection<E> collection) {
        if (collection == null) {
            return new EntryList<>();
        }
        return new EntryList<>(collection);
    }

    private final List<E> entries;

    public EntryList() {
        this.entries = new ArrayList<>();
    }

    public EntryList(final Collection<E> c) {
        this.entries = new ArrayList<>(c);
    }

    public int size() {
        return this.entries.size();
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public boolean contains(final Object o) {
        return this.entries.contains(o);
    }

    public Iterator<E> iterator() {
        return this.entries.iterator();
    }

    public Object[] toArray() {
        return this.entries.toArray();
    }

    public <T> T[] toArray(final T[] a) {
        return this.entries.toArray(a);
    }

    public boolean containsAll(final Collection<?> c) {
        return this.entries.containsAll(c);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EntryList<?> other = (EntryList<?>) obj;
        if (this.entries == null) {
            if (other.entries != null) {
                return false;
            }
        }
        if (other.entries == null) {
            return false;
        }
        if (this.entries.size() != other.entries.size()) {
            return false;
        }
        final Map<E, Long> collect1 =
                this.entries.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        final Map<?, Long> collect2 = other.entries.stream()
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        if (!collect1.equals(collect2)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.entries == null) ? 0
                : this.entries.stream()
                        .collect(Collectors.groupingBy(e -> e, Collectors.counting())).hashCode());
        return result;
    }

    public E get(final int index) {
        return this.entries.get(index);
    }

    public int indexOf(final Object o) {
        return this.entries.indexOf(o);
    }

    public int lastIndexOf(final Object o) {
        return this.entries.lastIndexOf(o);
    }

    public ListIterator<E> listIterator() {
        return this.entries.listIterator();
    }

    public ListIterator<E> listIterator(final int index) {
        return this.entries.listIterator(index);
    }

    public List<E> subList(final int fromIndex, final int toIndex) {
        return this.entries.subList(fromIndex, toIndex);
    }

    public void forEach(final Consumer<? super E> action) {
        this.entries.forEach(action);
    }

    public Stream<E> stream() {
        return this.entries.stream();
    }

    public Stream<E> parallelStream() {
        return this.entries.parallelStream();
    }

    public Spliterator<E> spliterator() {
        return this.entries.spliterator();
    }

    @Override
    public String toString() {
        return "EntryList [entries=" + this.entries + "]";
    }

}
