package com.example.budspaces.Utils;

import java.util.Objects;

/**
 * Created by ahmed on 02/05/20.
 */
public class Tuple3<T, U, V> {
    final T first;
    final U second;
    final V third;

    public Tuple3(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public boolean equals(Object o) {
        // Generated by IntelliJ
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple3<?, ?, ?> tuple3 = (Tuple3<?, ?, ?>) o;

        return first != null ? first.equals(tuple3.first) : tuple3.first == null &&
                (second != null ? second.equals(tuple3.second) : tuple3.second == null &&
                        (Objects.equals(third, tuple3.third)));

    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }

    public V getThird() {
        return third;
    }

    @Override
    public int hashCode() {
        // Generated by IntelliJ
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        result = 31 * result + (third != null ? third.hashCode() : 0);
        return result;
    }
}