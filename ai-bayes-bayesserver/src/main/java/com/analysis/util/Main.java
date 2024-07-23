package com.analysis.util;

import com.bayesserver.*;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Network network = new Network();

        // Add standard probability nodes...

        State oilDry = new State("Dry");
        State oilWet = new State("Wet");
        State oilSoaking = new State("Soaking");
        Variable oil = new Variable("Oil", oilDry, oilWet, oilSoaking);
        Node nodeOil = new Node(oil);
        network.getNodes().add(nodeOil);

        State drillYes = new State("Yes");
        State drillNo = new State("No");
        Variable drill = new Variable("Drill?", VariableValueType.DISCRETE, VariableKind.DECISION);
        drill.getStates().add(drillYes);
        drill.getStates().add(drillNo);
        Node nodeDrill = new Node(drill);
        network.getNodes().add(nodeDrill);

        List<Variable> fatherNodes = new ArrayList<>();
        fatherNodes.add(oil);
        fatherNodes.add(drill);

        List<List<State>> allCombinations = getAllCombinations(fatherNodes);

        // 打印所有状态组合
        for (List<State> combination : allCombinations) {
            System.out.println(combination);
        }
    }

    /**
     * 获取所有状态组合
     *
     * @param nodes
     * @return {@link List }<{@link List }<{@link State }>>
     */
    private static List<List<State>> getAllCombinations(List<Variable> nodes) {
        List<List<State>> result = new ArrayList<>();
        getAllCombinationsHelper(nodes, 0, new ArrayList<>(), result);
        return result;
    }

    /**
     * 获取所有状态组合
     *
     * @param nodes
     * @param depth
     * @param currentCombination
     * @param result
     */
    private static void getAllCombinationsHelper(
            List<Variable> nodes,
            int depth,
            List<State> currentCombination,
            List<List<State>> result) {
        if (depth == nodes.size()) {
            result.add(new ArrayList<>(currentCombination));
            return;
        }
        Variable currentNode = nodes.get(depth);
        List<State> states = currentNode.getStates();

        for (State state : states) {
            currentCombination.add(state);
            getAllCombinationsHelper(nodes, depth + 1, currentCombination, result);
            currentCombination.remove(currentCombination.size() - 1);
        }
    }
}