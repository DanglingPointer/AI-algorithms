package com.events;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Assignment5 ass = new Assignment5();

        Assignment5.CSP solver = ass.createSudokuCSP("veryhard.txt");
        Assignment5.VariablesToDomainsMapping solution = solver.backtrackingSearch();

        ass.printSudokuSolution(solution);
        System.out.println("Backtrack called " + solver.backtrackCallCount + " times");
        System.out.println("Backtrack returned fail " + solver.backtrackFailCount + " times");
    }
}
