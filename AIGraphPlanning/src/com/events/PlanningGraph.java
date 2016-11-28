package com.events;

import java.util.*;

/**
 * Created by Mikhail Vasilyev on 27.11.2016.
 */
public class PlanningGraph
{
    private List<Level> _graph;
    private final Set<Action> _allActions;
    private BackwardSearch _search;

    /**
     * Constructs initial level using 'initState'.
     * 'allActions' must include persistence actions
     */
    public PlanningGraph(List<Fluent> initState, Set<Action> allActions) {
        _allActions = allActions;
        _graph = new ArrayList<>();
        Level initLvl = new Level();
        for (Fluent f : initState)
            initLvl.addFluent(f);
        // persistence actions will be generated at 1st iteration
        _graph.add(initLvl);
        _search = new BackwardSearch(_graph);
    }
    /**
     * True of the graph has leveled off
     */
    public boolean leveledOff() {
        if (_graph.size() < 2)
            return false;
        Level last = _graph.get(_graph.size() - 1);
        Level prev = _graph.get(_graph.size() - 2);
        return last.equalToLevel(prev);
    }
    public boolean nogoodLeveledOff() {
        if (_search.nogood.size() < 2)
            return false;
        Nogood last = _search.nogood.get(_search.nogood.size() - 1);
        Nogood nextlast = _search.nogood.get(_search.nogood.size() - 2);
        return last.equals(nextlast);
    }
    /**
     * Compares the last level with the goal and checks mutexes
     */
    public boolean goalAchieved(List<Fluent> goal) {
        Level last = _graph.get(_graph.size() - 1);
        for (Fluent f : goal) {
            if (!last.containsFluent(f))
                return false;
        }
        for (int firstInd = 0; firstInd < goal.size(); ++firstInd) {
            for (int secondInd = firstInd; secondInd < goal.size(); ++secondInd) {
                if (last.containsMutex(goal.get(firstInd), goal.get(secondInd)))
                    return false;
            }
        }
        return true;
    }
    /**
     * Tries to extract plan from the graph
     *
     * @param goals: the final goals to be achieved
     * @return list of action sets for each stage/level or null if failure
     */
    public List<List<Action>> extractSolution(List<Fluent> goals) {
        if (_search.search(new HashSet<>(goals)))
            return _search.plan;
        return null;
    }
    /**
     * Expands graph adding A[i-1] and S[i] levels
     */
    public void generateLevel() {
        Level last = _graph.get(_graph.size() - 1);
        Level next = new Level();
        next.copyActions(last.getActions());
        next.copyFluents(last.getFluents());

        // generating actions
        for (Action a : _allActions) {
            if (!next.containsAction(a)) {
                boolean addAction = true;
                // Check that preconditions are there
                for (Fluent precond : a.getPrecond()) {
                    if (!last.containsFluent(precond)) {
                        addAction = false;
                        break;
                    }
                }
                // Check that preconditions are not mutexed
                if (addAction) {
                    for (Fluent precond1 : a.getPrecond()) {
                        for (Fluent precond2 : a.getPrecond()) {
                            if (last.containsMutex(precond1, precond2)) {
                                addAction = false;
                                break;
                            }
                        }
                        if (!addAction) break;
                    }
                }
                if (addAction)
                    next.addAction(a);
            }
        }

        // generating action mutexes
        for (Action a1 : next.getActions()) {
            for (Action a2 : next.getActions()) {
                if (a1 != a2) {
                    // Inconsistent effects
                    if (addMutexIfNegation(a1, a2, a1.getEffect(), a2.getEffect(), next))
                        continue;

                    // Interference
                    if (addMutexIfNegation(a1, a2, a1.getEffect(), a2.getPrecond(), next))
                        continue;
                    if (addMutexIfNegation(a1, a2, a1.getPrecond(), a2.getEffect(), next))
                        continue;

                    // Competing needs
                    boolean mutAdded = false;
                    for (Fluent precond1 : a1.getPrecond()) {
                        for (Fluent precond2 : a2.getPrecond()) {
                            if (last.containsMutex(precond1, precond2)) {
                                next.addMutex(a1, a2);
                                mutAdded = true;
                                break;
                            }
                        }
                        if (mutAdded) break;
                    }
                }
            }
        }

        // generating fluents
        for (Action a : next.getActions()) {
            for (Fluent effect : a.getEffect())
                next.addFluent(effect);
        }

        // generating fluent mutexes
        for (Fluent f1 : next.getFluents()) {
            for (Fluent f2 : next.getFluents()) {
                if (f1 != f2) {
                    // if negation
                    if (f1.isNegationOf(f2)) {
                        next.addMutex(f1, f2);
                        continue;
                    }

                    // if achieved by mutex'ed actions (inconsistent support)
                    Set<Action> f1Actions = new HashSet<>(); // actions giving f1
                    Set<Action> f2Actions = new HashSet<>(); // actions giving f2
                    for (Action a : next.getActions()) {
                        if (a.getEffect().contains(f1))
                            f1Actions.add(a);
                        if (a.getEffect().contains(f2))
                            f2Actions.add(a);
                    }

                    boolean addMutex = true;
                    for (Action a1 : f1Actions) {
                        for (Action a2 : f2Actions) {
                            if (!next.containsMutex(a1, a2)) {
                                addMutex = false;
                                break;
                            }
                        }
                        if (!addMutex) break;
                    }
                    if (addMutex)
                        next.addMutex(f1, f2);
                }
            }
        }
        _graph.add(next);
    }
    /**
     * Adds a mutex between a1 and a2 in lvl if one of the fluents in set1 is a negation of one of the fluents in set2
     *
     * @return true if mutex added
     */
    private static boolean addMutexIfNegation(Action a1, Action a2, Set<Fluent> set1, Set<Fluent> set2, Level lvl) {
        for (Fluent f1 : set1) {
            for (Fluent f2 : set2) {
                if (f1.isNegationOf(f2)) {
                    lvl.addMutex(a1, a2);
                    return true;
                }
            }
        }
        return false;
    }
}

/**
 * Helper class for extracting a plan from the graph. Uses depth-first search
 */
class BackwardSearch
{
    List<Level> graph;
    List<Nogood> nogood;
    List<List<Action>> plan;

    public BackwardSearch(List<Level> graph) {
        this.graph = graph;
        nogood = new ArrayList<>();
        plan = new ArrayList<List<Action>>(graph.size());
    }
    public boolean search(Set<Fluent> goals) {
        plan.clear();
        return dfs(goals, graph.size() - 1);
    }
    /**
     * Recursive depth-first searh that searches through the graph backwards
     *
     * @param goals
     * @param lvlInd
     * @return
     */
    private boolean dfs(Set<Fluent> goals, int lvlInd) {
        Level lvl = graph.get(lvlInd);

        if (nogood.contains(new Nogood(lvl, goals)))
            return false;

        // Finding all actions that have the fluent/goal as effect
        Map<Fluent, List<Action>> goalToActions = new HashMap<>();
        for (Fluent goal : goals) {
            List<Action> actions = new ArrayList<>();
            for (Action a : lvl.getActions()) {
                if (a.getEffect().contains(goal))
                    actions.add(a);
            }
            goalToActions.put(goal, actions);
            if (actions.size() == 0) {
                nogood.add(new Nogood(lvl, goals));
                return false;
            }
        }

        // Preparations for iterating through all possible nodes (actions combinations)
        int goalSize = goalToActions.size();
        int[] actionIndexes = new int[goalSize];
        int[] maxActionIndexes = new int[goalSize];
        int j = 0;
        for (List<Action> actionList : goalToActions.values()) {
            maxActionIndexes[j++] = actionList.size() - 1;
        }

        // Going through all possible combinations of actions (=nodes)
        do {
            // Creating node with the current actions' selection
            List<Action> node = new ArrayList<>();
            Collection<List<Action>> listOfDomains = goalToActions.values();
            int i = 0;
            for (List<Action> domain : listOfDomains)
                node.add(domain.get(actionIndexes[i++]));

            // Checking if the node is conflict-free
            boolean conflictFree = true;
            for (Action a1 : node) {
                for (Action a2 : node) {
                    if (lvl.containsMutex(a1, a2)) {
                        conflictFree = false;
                        break;
                    }
                }
                if (!conflictFree) break;
            }

            // Recursive call with new goal set if conflict-free
            if (conflictFree) {
                Set<Fluent> newGoals = new HashSet<>();
                for (Action a : node)
                    newGoals.addAll(a.getPrecond());

                if (lvlInd == 1 || dfs(newGoals, lvlInd - 1)) {
                    plan.add(lvlInd-1, node);
                    return true;
                }
            }

        } while (incrementIndex(actionIndexes, maxActionIndexes, goalSize - 1));

        // failure, no node fits
        nogood.add(new Nogood(lvl, goals));
        return false;
    }
    /**
     * Substitution for variable number of nested loops.
     * Goes through all combinations of indexes, within bounds of 0 and maxIndexes
     *
     * @param indexes    current combination of indices to be incremented
     * @param maxIndexes max values for corresponding indexes
     * @param indexId    must start with least significant index when called initially
     * @return false if not possible to increment further
     */
    private boolean incrementIndex(int[] indexes, int[] maxIndexes, int indexId) {
        if (indexes[indexId] == maxIndexes[indexId]) {
            if (indexId > 0)
                return incrementIndex(indexes, maxIndexes, indexId - 1);
            return false;
        } else {
            ++indexes[indexId];
            if (indexId < indexes.length - 1) {
                // all less significant indexes (if any) are set to 0
                for (int i = indexId + 1; i < indexes.length; ++i)
                    indexes[i] = 0;
            }
            return true;
        }
    }
}