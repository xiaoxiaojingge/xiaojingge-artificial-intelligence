package com.analysis.example;

import com.bayesserver.CLGaussian;
import com.bayesserver.Network;
import com.bayesserver.State;
import com.bayesserver.Variable;
import com.bayesserver.inference.*;

/**
 * 联合查询
 *
 * @author lijing
 * @date 2024-08-08
 */
public class JointQueryExample {

    public static void main(String[] args) throws Exception {

        Network network = new Network();

        // TODO download the network from the Bayes Server User Interface (or Bayes Server Online)
        // and adjust the following path
        network.load("Iris.bayes");

        Variable cluster = network.getVariables().get("Cluster", true);
        Variable sepalLength = network.getVariables().get("Sepal length", true);
        Variable sepalWidth = network.getVariables().get("Sepal width", true);
        Variable petalLength = network.getVariables().get("Petal length", true);
        Variable petalWidth = network.getVariables().get("Petal width", true);

        // The loaded network happens to be a mixture of multi-variate Gaussians
        // but could have additional/different structure

        InferenceFactory factory = new RelevanceTreeInferenceFactory();
        Inference inference = factory.createInferenceEngine(network);
        QueryOptions queryOptions = factory.createQueryOptions();
        QueryOutput queryOutput = factory.createQueryOutput();

        queryOptions.setLogLikelihood(true);    // set this to true to calculate the log(pdf) === log-likelihood

        Evidence evidence = inference.getEvidence();

        // set some evidence
        evidence.set(petalLength, 3.1);

        // query a uni-variate mixture of Gaussians

        CLGaussian queryMixture = new CLGaussian(new Variable[]{sepalLength, cluster});
        inference.getQueryDistributions().add(queryMixture);

        // also add a marginal query
        CLGaussian queryMarginal = new CLGaussian(sepalWidth);
        inference.getQueryDistributions().add(queryMarginal);

        // we could add other queries here ...

        inference.query(queryOptions, queryOutput);

        double logLikelihood = queryOutput.getLogLikelihood();

        System.out.println(String.format("Log-likelihood %f, pdf %f", logLikelihood, Math.exp(logLikelihood)));

        System.out.println();

        System.out.println("Conditional mixture of Gaussians...");

        for (State clusterState : cluster.getStates()) {

            double weight = queryMixture.getTable().get(clusterState);
            double mean = queryMixture.getMean(sepalLength, clusterState);
            double variance = queryMixture.getVariance(sepalLength, clusterState);

            System.out.println(String.format("Weight %f, mean %f, variance %f", weight, mean, variance));

        }

        System.out.println();

        System.out.println(String.format(
                "Sepal width | evidence, mean %f, variance %f",
                queryMarginal.getMean(sepalWidth),
                queryMarginal.getVariance(sepalWidth)));
    }
}
