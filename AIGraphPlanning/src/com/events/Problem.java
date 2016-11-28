package com.events;

import java.util.List;
import java.util.Set;
/**
 * Created by Mikhail Vasilyev on 27.11.2016.
 */

/**
 * Interface defining the problem to be solved using Graph-Plan
 */
public interface Problem
{
    /**
     * Returns all defined actions including persistence actions
     */
    Set<Action> getActions();

    /**
     * Returns all possible fluents, including negated ones
     */
    Set<Fluent> getFluents();

    /**
     * Returns the initial combination of fluents which must consist of some
     * of the same instances defined in getFluents()
     */
    List<Fluent> getInitState();

    /**
     * Returns the final combination of fluents to be achieved at the end.
     * It must consist of some of the same instances defined in getFluents()
     */
    List<Fluent> getGoals();
}
