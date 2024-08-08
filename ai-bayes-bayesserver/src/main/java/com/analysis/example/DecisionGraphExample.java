// --------------------------------------------------------------------------------------------------------------------
// <copyright file="DecisionGraphExample.java" company="Bayes Server">
//   Copyright (C) Bayes Server.  All rights reserved.
// </copyright>
// --------------------------------------------------------------------------------------------------------------------

package com.analysis.example;

import com.bayesserver.*;
import com.bayesserver.inference.*;

public class DecisionGraphExample {
    public static void main(String[] args) throws Exception {

        // In this example we will first construct the well known
        // Oil Wildcatter Decision Graph (Influence diagram) manually.
        // We could instead use network.Load(...) if we have an existing network.
        // We will then use the Single Policy Updating algorithm to
        // optimize decisions under uncertainty.

        Network network = new Network();

        // Add standard probability nodes...

        State oilDry = new State("Dry");
        State oilWet = new State("Wet");
        State oilSoaking = new State("Soaking");
        Variable oil = new Variable("Oil", oilDry, oilWet, oilSoaking);
        Node nodeOil = new Node(oil);
        network.getNodes().add(nodeOil);

        State testResultClosed = new State("Closed");
        State testResultOpen = new State("Open");
        State testResultDiffuse = new State("Diffuse");
        Variable testResult = new Variable("Test Result", testResultClosed, testResultOpen, testResultDiffuse);
        Node nodeTestResult = new Node(testResult);
        network.getNodes().add(nodeTestResult);

        // Add decision nodes...

        State testYes = new State("Yes");
        State testNo = new State("No");
        Variable test = new Variable("Test?", VariableValueType.DISCRETE, VariableKind.DECISION);
        test.getStates().add(testYes);
        test.getStates().add(testNo);
        Node nodeTest = new Node(test);
        network.getNodes().add(nodeTest);

        State drillYes = new State("Yes");
        State drillNo = new State("No");
        Variable drill = new Variable("Drill?", VariableValueType.DISCRETE, VariableKind.DECISION);
        drill.getStates().add(drillYes);
        drill.getStates().add(drillNo);
        Node nodeDrill = new Node(drill);
        network.getNodes().add(nodeDrill);

        // Add utility nodes...

        // Note that utility variables in Bayes Server are continuous.  They can even have variances.

        Variable drillUtility = new Variable("Drill utility", VariableValueType.CONTINUOUS, VariableKind.UTILITY);
        Node nodeDrillUtility = new Node(drillUtility);
        network.getNodes().add(nodeDrillUtility);

        Variable testUtility = new Variable("Test utility", VariableValueType.CONTINUOUS, VariableKind.UTILITY);
        Node nodeTestUtility = new Node(testUtility);
        network.getNodes().add(nodeTestUtility);

        // When a network has more than one utility node
        // we need to add a further (leaf) utility node which
        // both determines how the other utilities are to be combined
        // and also provides a means of querying the maximum expected utility.
        // We can even perform joint queries.

        Variable meu = new Variable("MEU", VariableValueType.CONTINUOUS, VariableKind.UTILITY);
        Node nodeMeu = new Node(meu);
        network.getNodes().add(nodeMeu);


        // Add the links

        NetworkLinkCollection links = network.getLinks();
        links.add(new Link(nodeOil, nodeTestResult));
        links.add(new Link(nodeOil, nodeDrillUtility));
        links.add(new Link(nodeTestResult, nodeDrill));
        links.add(new Link(nodeTest, nodeTestResult));
        links.add(new Link(nodeTest, nodeDrill));
        links.add(new Link(nodeTest, nodeTestUtility));
        links.add(new Link(nodeDrill, nodeDrillUtility));
        links.add(new Link(nodeDrillUtility, nodeMeu));
        links.add(new Link(nodeTestUtility, nodeMeu));


        // Here we will manually specify the distributions
        // but we could also learn them from data

        Table tableOil = nodeOil.newDistribution().getTable();
        tableOil.set(0.5, oilDry);
        tableOil.set(0.3, oilWet);
        tableOil.set(0.2, oilSoaking);
        nodeOil.setDistribution(tableOil);

        Table tableTestResult = nodeTestResult.newDistribution().getTable();

        // We could set each value as we did for the previous distribution
        // however because there are quite a few values we will use
        // a table iterator

        double third = 1.0 / 3.0;

        new TableIterator(
                tableTestResult,
                new Node[]{nodeOil, nodeTest, nodeTestResult}
        ).copyFrom(
                new double[]{
                        0.1, 0.3, 0.6, third, third, third, 0.3, 0.4, 0.3, third, third, third, 0.5, 0.4, 0.1, third, third, third});

        nodeTestResult.setDistribution(tableTestResult);

        Table tableTest = nodeTest.newDistribution().getTable();
        tableTest.normalize(true);  // set to uniform distribution
        nodeTest.setDistribution(tableTest);

        Table tableDrill = nodeDrill.newDistribution().getTable();
        tableDrill.normalize(true); // set to uniform distribution
        nodeDrill.setDistribution(tableDrill);

        // In the oil wildcatter example, all utilities have zero variance (point Gaussians)
        // however Bayes Server supports utility distributions with variances.
        // In fact, if you learn the distributions from data they will typically have
        // non-zero variances.

        CLGaussian gaussianDrillUtility = (CLGaussian) nodeDrillUtility.newDistribution();
        gaussianDrillUtility.setMean(drillUtility, -70.0, oilDry, drillYes);
        gaussianDrillUtility.setMean(drillUtility, 0.0, oilDry, drillNo);
        gaussianDrillUtility.setMean(drillUtility, 50.0, oilWet, drillYes);
        gaussianDrillUtility.setMean(drillUtility, 0.0, oilWet, drillNo);
        gaussianDrillUtility.setMean(drillUtility, 200.0, oilSoaking, drillYes);
        gaussianDrillUtility.setMean(drillUtility, 0.0, oilSoaking, drillNo);
        nodeDrillUtility.setDistribution(gaussianDrillUtility);

        CLGaussian gaussianTestUtility = (CLGaussian) nodeTestUtility.newDistribution();
        gaussianTestUtility.setMean(testUtility, -10.0, testYes);
        gaussianTestUtility.setMean(testUtility, 0.0, testNo);
        nodeTestUtility.setDistribution(gaussianTestUtility);

        // The MEU utility defines how the utilities are combined.
        // In this example we just add them, by giving each parent a weight of 1
        CLGaussian gaussianMeu = (CLGaussian) nodeMeu.newDistribution();
        gaussianMeu.setWeight(meu, drillUtility, 1.0);
        gaussianMeu.setWeight(meu, testUtility, 1.0);
        nodeMeu.setDistribution(gaussianMeu);


        // Now the network structure and distributions are fully specified

        // Next, lets query the network.

        InferenceFactory factory = new RelevanceTreeInferenceFactory();
        Inference inference = factory.createInferenceEngine(network);
        QueryOptions queryOptions = factory.createQueryOptions();
        QueryOutput queryOutput = factory.createQueryOutput();

        // We want to optimize the decisions under uncertainty so will
        // use the Single Policy Updating algorithm (SPU)
        queryOptions.setDecisionAlgorithm(DecisionAlgorithm.SINGLE_POLICY_UPDATING_LIGHT);

        Table queryOil = new Table(oil);    // query a probability variable
        Table queryDrill = new Table(drill);  // query a decision variable
        CLGaussian queryMeu = new CLGaussian(meu); // get the Maximum Expected Utility (MEU)
        CLGaussian queryJoint_meu_oil = new CLGaussian(new Variable[]{meu, oil});   // we can also query joint distributions.
        CLGaussian queryJoint_meu_testResult = new CLGaussian(new Variable[]{meu, testResult});   // we can also query joint distributions.

        QueryDistributionCollection queryDistributions = inference.getQueryDistributions();
        queryDistributions.add(queryOil);
        queryDistributions.add(queryDrill);
        queryDistributions.add(queryMeu);
        queryDistributions.add(queryJoint_meu_oil);
        queryDistributions.add(queryJoint_meu_testResult);

        // If we have any evidence to set use
        // inference.Evidence.Set or inference.Evidence.SetState
        // here

        inference.query(queryOptions, queryOutput);

        double oilDryValue = queryOil.get(oilDry);
        System.out.println(String.format("Oil = Dry\t%s", oilDryValue));   // expected 0.5

        double meuValue = queryMeu.getMean(meu);
        System.out.println(String.format("MEU\t%s", meuValue));   // expected value 22.5

        double drillYesValue = queryDrill.get(drillYes);
        System.out.println(String.format("Drill? = Yes\t%s", drillYesValue));   // expected 0.59

        double meuOilDry = queryJoint_meu_oil.getMean(meu, oilDry);
        System.out.println(String.format("MEU Oil=Dry\t%s", meuOilDry));   // expected -38.0


        for (State state : oil.getStates()) {
            double weight = queryJoint_meu_oil.getTable().get(state);
            double mean = queryJoint_meu_oil.getMean(meu, state);
            double variance = queryJoint_meu_oil.getVariance(meu, state);
            System.out.println(state);
            System.out.println(String.format("Weight %f, mean %f, variance %f", weight, mean, variance));

        }

        for (State state : testResult.getStates()) {
            double weight = queryJoint_meu_testResult.getTable().get(state);
            double mean = queryJoint_meu_testResult.getMean(meu, state);
            double variance = queryJoint_meu_testResult.getVariance(meu, state);
            System.out.println(state);
            System.out.println(String.format("Weight %f, mean %f, variance %f", weight, mean, variance));

        }
    }
}