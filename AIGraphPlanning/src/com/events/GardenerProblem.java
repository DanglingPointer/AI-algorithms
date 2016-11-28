package com.events;

import java.util.*;

/**
 * Created by Mikhail Vasilyev on 27.11.2016.
 */
public class GardenerProblem implements Problem
{
    List<Fluent> _fluents;
    List<Action> _actions;
    List<Fluent> _initState;
    List<Fluent> _goals;

    public GardenerProblem() {
        _fluents = Arrays.asList(
                new Fluent("availSprk", true),  // 0
                new Fluent("longLawn", true),   // 1
                new Fluent("dryLawn", true),    // 2
                new Fluent("dryFlwrs", true),   // 3
                new Fluent("wetLawn", true),    // 4
                new Fluent("wetFlwrs", true),   // 5
                new Fluent("shortLawn", true),  // 6

                new Fluent("availSprk", false), // 7
                new Fluent("longLawn", false),  // 8
                new Fluent("dryLawn", false),   // 9
                new Fluent("dryFlwrs", false),  // 10
                new Fluent("wetLawn", false),   // 11
                new Fluent("wetFlwrs", false),  // 12
                new Fluent("shortLawn", false)  // 13
        );
        _initState = Arrays.asList(
                _fluents.get(0), _fluents.get(1), _fluents.get(3), _fluents.get(2)
        );
        _goals = Arrays.asList(_fluents.get(6), _fluents.get(4), _fluents.get(5));

        _actions = new ArrayList<>();
        Action mowLawn = new Action("mowLawn",
                new Fluent[]{_fluents.get(2), _fluents.get(1)},
                new Fluent[]{_fluents.get(6), _fluents.get(8)}
        );
        Action sprkLawn = new Action("sprkLawn",
                new Fluent[]{_fluents.get(0), _fluents.get(2)},
                new Fluent[]{_fluents.get(4), _fluents.get(7), _fluents.get(9)}
        );
        Action sprkFlwrs = new Action("sprkFlwrs",
                new Fluent[]{_fluents.get(0), _fluents.get(3)},
                new Fluent[]{_fluents.get(5), _fluents.get(7), _fluents.get(10)}
        );
        Action wcanFlwrs = new Action("wcanFlwrs",
                new Fluent[]{_fluents.get(3)},
                new Fluent[]{_fluents.get(5), _fluents.get(10)}
        );
        Collections.addAll(_actions, mowLawn, sprkLawn, sprkFlwrs, wcanFlwrs);
        for (Fluent f : _fluents) {
            // Persistence actions
            _actions.add(new Action(f.name, new Fluent[]{f}, new Fluent[]{f}));
        }
    }
    /**
     * All fluents, including negations. Immutable
     */
    public Set<Fluent> getFluents() {
        return Collections.unmodifiableSet(new HashSet<>(_fluents));
    }
    /**
     * All actions including persistence actions. Immutable
     */
    public Set<Action> getActions() {
        return Collections.unmodifiableSet(new HashSet<>(_actions));
    }
    /**
     * Initial fluents
     */
    public List<Fluent> getInitState() {
        return _initState;
    }
    /**
     * Final combination of fluents to be achieved
     */
    public List<Fluent> getGoals() {
        return _goals;
    }
}
