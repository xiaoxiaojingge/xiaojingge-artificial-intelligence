package com.analysis.example;

import com.bayesserver.*;
import com.bayesserver.inference.*;

/**
 * @author lijing
 * @date 2024-08-08
 */
public class Test {

    public static void main(String[] args) throws InconsistentEvidenceException {
        Network network = new Network();
        Variable decision = new Variable("decision", VariableValueType.DISCRETE, VariableKind.DECISION);
        State state1 = new State("State1");
        State state2 = new State("State2");
        decision.getStates().add(state1);
        decision.getStates().add(state2);
        Node decisionNode = new Node(decision);

        Variable decision2 = new Variable("decision2", VariableValueType.DISCRETE, VariableKind.DECISION);
        State state11 = new State("State11");
        State state22 = new State("State22");
        decision2.getStates().add(state11);
        decision2.getStates().add(state22);
        Node decisionNode2 = new Node(decision2);

        Variable utility = new Variable("utility", VariableValueType.CONTINUOUS, VariableKind.UTILITY);
        Node utilityNode = new Node(utility);

        network.getNodes().add(decisionNode);
        network.getNodes().add(decisionNode2);
        network.getNodes().add(utilityNode);

        NetworkLinkCollection links = network.getLinks();
        links.add(new Link(decisionNode, utilityNode));
        links.add(new Link(decisionNode2, decisionNode));

        Table tableTest = decisionNode.newDistribution().getTable();
        tableTest.normalize(true);  // set to uniform distribution
        decisionNode.setDistribution(tableTest);

        Table tableTest2 = decisionNode2.newDistribution().getTable();
        tableTest2.normalize(true);  // set to uniform distribution
        decisionNode2.setDistribution(tableTest2);

        CLGaussian gaussianTestUtility = (CLGaussian) utilityNode.newDistribution();
        gaussianTestUtility.setMean(utility, 79, state1);
        gaussianTestUtility.setMean(utility, 40, state2);
        utilityNode.setDistribution(gaussianTestUtility);

        ValidationOptions validationOptions = new ValidationOptions();
        network.validate(validationOptions);


        InferenceFactory factory = new RelevanceTreeInferenceFactory();
        Inference inference = factory.createInferenceEngine(network);
        QueryOptions queryOptions = factory.createQueryOptions();
        QueryOutput queryOutput = factory.createQueryOutput();

        // use the Single Policy Updating algorithm (SPU)
        queryOptions.setDecisionAlgorithm(DecisionAlgorithm.SINGLE_POLICY_UPDATING_LIGHT);

        CLGaussian queryJoint = new CLGaussian(new Variable[]{utility, decision});
        CLGaussian queryJoint2 = new CLGaussian(new Variable[]{utility, decision2});

        QueryDistributionCollection queryDistributions = inference.getQueryDistributions();
        queryDistributions.add(queryJoint);
        queryDistributions.add(queryJoint2);


        inference.query(queryOptions, queryOutput);

        for (State state : decision.getStates()) {
            double weight = queryJoint.getTable().get(state);
            if (weight == 1 || weight == 0) {
                double mean1 = gaussianTestUtility.getMean(0, 0);
                double mean2 = gaussianTestUtility.getMean(1, 0);
                System.out.println(mean1);
                System.out.println(mean2);
                break;
            } else {
                double mean = queryJoint.getMean(utility, state);
                double variance = queryJoint.getVariance(utility, state);
                System.out.println(String.format("Weight %f, mean %f, variance %f", weight, mean, variance));
            }
        }

        for (State state : decision2.getStates()) {
            double weight = queryJoint2.getTable().get(state);
            if (weight == 1 || weight == 0) {
                double mean1 = gaussianTestUtility.getMean(0, 0);
                double mean2 = gaussianTestUtility.getMean(1, 0);
                System.out.println(mean1);
                System.out.println(mean2);
                break;
            } else {
                double mean = queryJoint2.getMean(utility, state);
                double variance = queryJoint2.getVariance(utility, state);
                System.out.println(String.format("Weight %f, mean %f, variance %f", weight, mean, variance));
            }
        }

    }
}
