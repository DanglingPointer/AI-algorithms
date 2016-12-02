package com.events;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Mikhail Vasilyev on 27.11.2016.
 */
public class Action
{
    private Set<Fluent> _precond;
    private Set<Fluent> _effect;
    private String _name;

    public Action(String name)
    {
        _precond = new HashSet<>();
        _effect = new HashSet<>();
        _name = name;
    }
    public Action(String name, Fluent[] preconds, Fluent[] effects)
    {
        _precond = new HashSet<>();
        _effect = new HashSet<>();
        Collections.addAll(_precond, preconds);
        Collections.addAll(_effect, effects);
        _name = name;
    }
    public String getName()
    {
        return _name;
    }
    public Set<Fluent> getPrecond()
    {
        return Collections.unmodifiableSet(_precond);
    }
    public Set<Fluent> getEffect()
    {
        return Collections.unmodifiableSet(_effect);
    }
    @Override
    public boolean equals(Object o)
    {
        Action a = (Action) o;
        return _precond.equals(a._precond) && _effect.equals(a._effect) && _name.equals(a._name);
    }
}
