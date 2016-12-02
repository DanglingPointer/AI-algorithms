package com.events;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Mikhail Vasilyev on 27.11.2016.
 * Level contains actions A[i] and fluents S[i+1]
 */
public class Level
{
    private Set<Fluent> _fluentSet;
    private Set<Pair<Fluent>> _fluentMutexes;

    private Set<Action> _actions;
    private Set<Pair<Action>> _actionMutexes;

    @Override
    public boolean equals(Object o)
    {
        Level rhs = (Level) o;
        return rhs._fluentSet.equals(_fluentSet) && rhs._fluentMutexes.equals(_fluentMutexes) &&
                rhs._actions.equals(_actions) && rhs._actionMutexes.equals(_actionMutexes);
    }
    /**
     * Creates an empty level
     */
    public Level()
    {
        _fluentSet = new HashSet<>();
        _fluentMutexes = new HashSet<>();
        _actions = new HashSet<>();
        _actionMutexes = new HashSet<>();
    }
    /**
     * Checks if two consecutive levels have leveled off
     *
     * @param prev: Previous level
     * @return 'true' if fluent sets are equal (regardless of mutexes)
     */
    public boolean equalToLevel(Level prev)
    {
        if (_fluentSet.size() != prev._fluentSet.size() ||
                _fluentMutexes.size() != prev._fluentMutexes.size())
            return false;
        for (Fluent f : prev._fluentSet) {
            if (!_fluentSet.contains(f))
                return false;
        }
        for (Pair<Fluent> mut : prev._fluentMutexes) {
            if (!_fluentMutexes.contains(mut))
                return false;
        }
        return true;
    }
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public boolean containsAction(Action a)
    {
        return _actions.contains(a);
    }
    public void addAction(Action a)
    {
        _actions.add(a);
    }
    public Set<Action> getActions()
    {
        return Collections.unmodifiableSet(_actions);
    }
    public void copyActions(Set<Action> from)
    {
        _actions.addAll(from);
    }
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public boolean containsFluent(Fluent f)
    {
        return _fluentSet.contains(f);
    }
    public void addFluent(Fluent f)
    {
        _fluentSet.add(f);
    }
    public Set<Fluent> getFluents()
    {
        return Collections.unmodifiableSet(_fluentSet);
    }
    public void copyFluents(Set<Fluent> from)
    {
        _fluentSet.addAll(from);
    }
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public boolean containsMutex(Fluent f1, Fluent f2)
    {
        if (!_fluentSet.contains(f1) || !_fluentSet.contains(f2))
            return false;
        for (Pair<Fluent> pair : _fluentMutexes) {
            if ((pair.first.equals(f1) && pair.second.equals(f2)) ||
                    (pair.first.equals(f2) && pair.second.equals(f1)))
                return true;
        }
        return false;
    }
    public boolean containsMutex(Action a1, Action a2)
    {
        if (!_actions.contains(a1) || !_actions.contains(a2))
            return false;
        for (Pair<Action> pair : _actionMutexes) {
            if ((pair.first.equals(a1) && pair.second.equals(a2)) ||
                    (pair.first.equals(a2) && pair.second.equals(a1)))
                return true;
        }
        return false;
    }
    public void addMutex(Fluent f1, Fluent f2)
    {
        _fluentMutexes.add(new Pair<>(f1, f2));
    }
    public void addMutex(Action a1, Action a2)
    {
        _actionMutexes.add(new Pair<>(a1, a2));
    }
}
