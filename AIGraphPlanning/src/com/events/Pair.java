package com.events;

import java.lang.*;
/**
 * Created by Mikhail Vasilyev on 27.11.2016.
 */

/**
 * Unordered pair
 */
public class Pair<T>
{
    public final T first;
    public final T second;

    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }
    @Override
    public int hashCode() {
        return first.hashCode() ^ second.hashCode();
    }
    @Override
    public boolean equals(Object o){
        Pair<T> rhs = (Pair<T>)o;
        return (first.equals(rhs.first) && second.equals(rhs.second)) ||
                (first.equals(rhs.second) && second.equals(rhs.first));
    }
}
