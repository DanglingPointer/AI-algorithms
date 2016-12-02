package com.events;

import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        // write your code here
        Problem gardener = new GardenerProblem();
        Graphplan solver = new Graphplan(gardener);
        List<List<Action>> solution = solver.Solve();
        if (solution != null) {
            int stage = 0;
            for (List<Action> parallelActions : solution) {
                System.out.print("Stage " + (stage++) + ": ");
                for (Action a : parallelActions) {
                    System.out.print(a.getName() + ' ');
                }
                System.out.println();
            }
        }
    }
}
