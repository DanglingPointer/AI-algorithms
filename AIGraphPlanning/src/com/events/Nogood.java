package com.events;

import java.util.Set;

/**
 * Created by Mikhail Vasilyev on 27.11.2016.
 */

/**
 * Represents the (level, goals) pair, where the 'goals' are the goals at 'level'
 */
public class Nogood
{
    public Nogood(Level level, Set<Fluent> goals) {
        this.level = level;
        this.goals = goals;
    }
    public Level level;
    public Set<Fluent> goals;

    @Override
    public boolean equals(Object o) {
        Nogood rhs = (Nogood) o;
        return level.equals(rhs.level) && goals.equals(rhs.goals);
    }
}
