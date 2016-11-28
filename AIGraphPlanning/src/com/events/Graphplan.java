package com.events;

import java.util.List;

/**
 * Created by Mikhail Vasilyev on 28.11.2016.
 */
public class Graphplan
{
    private Problem _problem;
    private PlanningGraph _graph;

    public Graphplan(Problem prob) {
        _problem = prob;
        _graph = new PlanningGraph(prob.getInitState(), prob.getActions());
    }
    /**
     * Returns solution or null
     */
    public List<List<Action>> Solve() {
        for(;;){
            if (_graph.goalAchieved(_problem.getGoals())){
                List<List<Action>> solution = _graph.extractSolution(_problem.getGoals());
                if (solution != null)
                    return solution;
            }
            if (_graph.leveledOff() && _graph.nogoodLeveledOff())
                return null;
            _graph.generateLevel();
        }
    }
}
