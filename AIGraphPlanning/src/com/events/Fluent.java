package com.events;

import java.lang.*;

/**
 * Created by Mikhail Vasilyev on 27.11.2016.
 */
public class Fluent
{
    public final String name;
    public final boolean value;

    public Fluent(String name, boolean value)
    {
        this.name = name;
        this.value = value;
    }

    public boolean isNegationOf(Fluent f)
    {
        return name.equals(f.name) && value != f.value;
    }
    @Override
    public boolean equals(Object o)
    {
        Fluent rhs = (Fluent) o;
        return name.equals(rhs.name) && value == rhs.value;
    }
}
