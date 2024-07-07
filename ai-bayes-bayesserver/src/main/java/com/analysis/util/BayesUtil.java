package com.analysis.util;

import cn.hutool.core.util.StrUtil;
import com.bayesserver.*;
import com.bayesserver.analysis.ParameterReference;
import com.bayesserver.analysis.SensitivityFunctionOneWay;
import com.bayesserver.analysis.SensitivityToParameters;
import com.bayesserver.inference.*;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * 贝叶斯工具类
 *
 * @author lijing
 * @date 2024-07-06
 */
public class BayesUtil {


    public static void saveBayes(String fileName) throws XMLStreamException, IOException {
        // Network network = new Network();
        // network.save(fileName);


        Node a = new Node();

    }

    /**
     * 贝叶斯官方示例
     *
     * @throws IOException
     * @throws XMLStreamException
     * @throws InconsistentEvidenceException
     */
    public static void officialExample() throws IOException, XMLStreamException, InconsistentEvidenceException {

        // In this example we programatically create a simple Bayesian network.
        // Note that you can automatically define nodes from data using
        // classes in BayesServer.Data.Discovery,
        // and you can automatically learn the parameters using classes in
        // BayesServer.Learning.Parameters,
        // however here we build a Bayesian network from scratch.

        Network network = new Network("Demo");

        // add the nodes (variables)

        State aTrue = new State("True");
        State aFalse = new State("False");
        Node a = new Node("A", aTrue, aFalse);

        State bTrue = new State("True");
        State bFalse = new State("False");
        Node b = new Node("B", bTrue, bFalse);

        State cTrue = new State("True");
        State cFalse = new State("False");
        Node c = new Node("C", cTrue, cFalse);

        State dTrue = new State("True");
        State dFalse = new State("False");
        Node d = new Node("D", dTrue, dFalse);

        network.getNodes().add(a);
        network.getNodes().add(b);
        network.getNodes().add(c);
        network.getNodes().add(d);

        // add some directed links

        network.getLinks().add(new Link(a, b));
        network.getLinks().add(new Link(a, c));
        network.getLinks().add(new Link(b, d));
        network.getLinks().add(new Link(c, d));

        // at this point we have fully specified the structural (graphical) specification of the Bayesian Network.

        // We must define the necessary probability distributions for each node.

        // Each node in a Bayesian Network requires a probability distribution conditioned on it's parents.

        // newDistribution() can be called on a Node to create the appropriate probability distribution for a node
        // or it can be created manually.

        // The interface Distribution has been designed to represent both discrete and continuous variables,

        // As we are currently dealing with discrete distributions, we will use the
        // Table class.

        // To access the discrete part of a distribution, we use Distribution.Table.

        // The Table class is used to define distributions over a number of discrete variables.

        Table tableA = a.newDistribution().getTable();     // access the table property of the Distribution

        // IMPORTANT
        // Note that calling Node.newDistribution() does NOT assign the distribution to the node.
        // A distribution cannot be assigned to a node until it is correctly specified.
        // If a distribution becomes invalid  (e.g. a parent node is added), it is automatically set to null.

        // as node A has no parents there is no ambiguity about the order of variables in the distribution
        tableA.set(0.1, aTrue);
        tableA.set(0.9, aFalse);

        // now tableA is correctly specified we can assign it to Node A;
        a.setDistribution(tableA);


        // node B has node A as a parent, therefore its distribution will be P(B|A)

        Table tableB = b.newDistribution().getTable();
        tableB.set(0.2, aTrue, bTrue);
        tableB.set(0.8, aTrue, bFalse);
        tableB.set(0.15, aFalse, bTrue);
        tableB.set(0.85, aFalse, bFalse);
        b.setDistribution(tableB);


        // specify P(C|A)
        Table tableC = c.newDistribution().getTable();
        tableC.set(0.3, aTrue, cTrue);
        tableC.set(0.7, aTrue, cFalse);
        tableC.set(0.4, aFalse, cTrue);
        tableC.set(0.6, aFalse, cFalse);
        c.setDistribution(tableC);


        // specify P(D|B,C)
        Table tableD = d.newDistribution().getTable();

        // we could specify the values individually as above, or we can use a TableIterator as follows
        TableIterator iteratorD = new TableIterator(tableD, new Node[]{b, c, d});
        iteratorD.copyFrom(new double[]{0.4, 0.6, 0.55, 0.45, 0.32, 0.68, 0.01, 0.99});
        d.setDistribution(tableD);


        // The network is now fully specified

        // If required the network can be saved...

        if (false)   // change this to true to save the network
        {
            network.save("fileName.bayes");  // replace 'fileName.bayes' with your own path
        }

        // Now we will calculate P(A|D=True), i.e. the probability of A given the evidence that D is true

        // use the factory design pattern to create the necessary inference related objects
        InferenceFactory factory = new RelevanceTreeInferenceFactory();
        Inference inference = factory.createInferenceEngine(network);
        QueryOptions queryOptions = factory.createQueryOptions();
        QueryOutput queryOutput = factory.createQueryOutput();

        // we could have created these objects explicitly instead, but as the number of algorithms grows
        // this makes it easier to switch between them

        inference.getEvidence().setState(dTrue);  // set D = True

        Table queryA = new Table(a);
        inference.getQueryDistributions().add(queryA);
        inference.query(queryOptions, queryOutput); // note that this can raise an exception (see help for details)

        System.out.println("P(A|D=True) = {" + queryA.get(aTrue) + "," + queryA.get(aFalse) + "}.");

        // Expected output ...
        // P(A|D=True) = {0.0980748663101604,0.90192513368984}

        // to perform another query we reuse all the objects

        // now lets calculate P(A|D=True, C=True)
        inference.getEvidence().setState(cTrue);

        // we will also return the log-likelihood of the case
        queryOptions.setLogLikelihood(true); // only request the log-likelihood if you really need it, as extra computation is involved

        inference.query(queryOptions, queryOutput);
        System.out.println(String.format("P(A|D=True, C=True) = {%s,%s}, log-likelihood = %s.", queryA.get(aTrue), queryA.get(aFalse), queryOutput.getLogLikelihood()));

        // Expected output ...
        // P(A|D=True, C=True) = {0.0777777777777778,0.922222222222222}, log-likelihood = -2.04330249506396.


        // Note that we can also calculate joint queries such as P(A,B|D=True,C=True)

    }


    /**
     * 本人测试示例
     *
     * @throws XMLStreamException
     * @throws IOException
     * @throws InconsistentEvidenceException
     */
    public static void testExample() throws XMLStreamException, IOException, InconsistentEvidenceException {

        // 网络对象
        Network network = new Network("测试贝叶斯网络");

        // a节点
        State aState1 = new State("橙");
        State aState2 = new State("蓝");
        State aState3 = new State("红");

        // Node a = new Node("暴雨", aState1, aState2, aState3);

        Variable aVar = new Variable("暴雨", VariableValueType.DISCRETE, VariableKind.PROBABILITY);
        aVar.getStates().add(aState1);
        aVar.getStates().add(aState2);
        aVar.getStates().add(aState3);
        Node a = new Node(aVar);


        // b节点
        State bState1 = new State("大");
        State bState2 = new State("小");

        // Node b = new Node("冲击力大小", bState1, bState2);

        Variable bVar = new Variable("冲击力大小", VariableValueType.DISCRETE, VariableKind.PROBABILITY);
        bVar.getStates().add(bState1);
        bVar.getStates().add(bState2);
        Node b = new Node(bVar);

        // c节点
        State cState1 = new State("是");
        State cState2 = new State("否");

        // Node c = new Node("泥石流", cState1, cState2);

        Variable cVar = new Variable("泥石流", VariableValueType.DISCRETE, VariableKind.PROBABILITY);
        cVar.getStates().add(cState1);
        cVar.getStates().add(cState2);
        Node c = new Node(cVar);

        // 添加节点
        network.getNodes().add(a);
        network.getNodes().add(b);
        network.getNodes().add(c);

        // 添加连线
        network.getLinks().add(new Link(a, c));
        network.getLinks().add(new Link(b, c));

        // 模型验证
        // ValidationOptions validationOptions = new ValidationOptions();
        // network.validate(validationOptions);

        // 添加条件概率
        Table tableA = a.newDistribution().getTable();
        tableA.set(0.40, aState1);
        tableA.set(0.35, aState2);
        tableA.set(0.25, aState3);
        a.setDistribution(tableA);

        Table tableB = b.newDistribution().getTable();
        tableB.set(0.30, bState1);
        tableB.set(0.70, bState2);
        b.setDistribution(tableB);

        Table tableC = c.newDistribution().getTable();
        TableIterator iteratorC = new TableIterator(tableC, new Node[]{a, b, c});
        iteratorC.copyFrom(new double[]{0.8, 0.2, 0.6, 0.4, 0.6, 0.4, 0.8, 0.2, 0.8, 0.2, 0.6, 0.4});
        c.setDistribution(tableC);

        // network.save("测试贝叶斯网络.bayes");
        InferenceFactory factory = new RelevanceTreeInferenceFactory();
        Inference inference = factory.createInferenceEngine(network);
        QueryOptions queryOptions = factory.createQueryOptions();
        QueryOutput queryOutput = factory.createQueryOutput();

        // 设置证据节点，即该状态概率为100%
        // inference.getEvidence().setState(aState1);
        // inference.getEvidence().set(a, 1.0);

        Table queryA = new Table(a);
        inference.getQueryDistributions().add(queryA);

        Table queryB = new Table(b);
        inference.getQueryDistributions().add(queryB);

        Table queryC = new Table(c);
        inference.getQueryDistributions().add(queryC);

        inference.query(queryOptions, queryOutput);

        System.out.println(queryA.get(aState1));
        System.out.println(queryA.get(aState2));

        System.out.println(queryB.get(bState1));
        System.out.println(queryB.get(bState2));

        System.out.println(queryC.get(cState1));
        System.out.println(queryC.get(cState2));


        // 敏感性分析
        // 证据对象
        DefaultEvidence evidence = new DefaultEvidence(network);
        SensitivityToParameters sensitivity = new SensitivityToParameters(network, factory);

        // 可以变化的单个参数，参数节点
        // 注意这里的 new State[]{aState1, bState1, cState1}，表明是个组合
        // 即条件概率表中选定 aState1，bState1，cState1 的组合的概率的那个参数
        ParameterReference parameter = new ParameterReference(c, new State[]{aState1, bState1, cState1});

        SensitivityFunctionOneWay oneWay = sensitivity.oneWay(
                evidence,
                // 假设节点的状态
                cState1,
                parameter
        );
        // 以上代码表示 假设 cState1 ， ParameterReference 为变化参数，计算假设状态如何根据ParameterReference的更改而变化。

        // System.out.println(StrUtil.format("Parameter value = {}", oneWay.getParameterValue()));
        // System.out.println(StrUtil.format("Sensitivity value = {}", oneWay.getSensitivityValue()));
        // System.out.println(StrUtil.format("P(Abnormal | e) = {}", oneWay.getProbabilityHypothesisGivenEvidence()));
        // System.out.println(StrUtil.format("Alpha = {}", oneWay.getAlpha()));
        // System.out.println(StrUtil.format("Beta = {}", oneWay.getBeta()));
        // System.out.println(StrUtil.format("Delta = {}", oneWay.getDelta()));
        // System.out.println(StrUtil.format("Gamma = {oneWay.Gamma}", oneWay.getGamma()));
        // System.out.println(StrUtil.format("Eval(0.2) = {}", oneWay.evaluate(0.2)));
        // System.out.println(StrUtil.format("Eval'(0.2) = {}", oneWay.evaluateDeriv(0.2)));
    }

    /**
     * 报警示例
     *
     * @throws XMLStreamException
     * @throws IOException
     * @throws InconsistentEvidenceException
     */
    public static void alarmExample() throws XMLStreamException, IOException, InconsistentEvidenceException {

        // 网络对象
        Network network = new Network("报警");

        // a节点
        State aState1 = new State("是");
        State aState2 = new State("否");
        Node a = new Node("发生抢劫", aState1, aState2);

        // b节点
        State bState1 = new State("是");
        State bState2 = new State("否");
        Node b = new Node("发生地震", bState1, bState2);

        // c节点
        State cState1 = new State("是");
        State cState2 = new State("否");
        Node c = new Node("报警", cState1, cState2);

        // d节点
        State dState1 = new State("是");
        State dState2 = new State("否");
        Node d = new Node("约翰尖叫", dState1, dState2);

        // e节点
        State eState1 = new State("是");
        State eState2 = new State("否");
        Node e = new Node("玛丽尖叫", eState1, eState2);

        // 添加节点
        network.getNodes().add(a);
        network.getNodes().add(b);
        network.getNodes().add(c);
        network.getNodes().add(d);
        network.getNodes().add(e);

        // 添加连线
        network.getLinks().add(new Link(a, c));
        network.getLinks().add(new Link(b, c));
        network.getLinks().add(new Link(c, d));
        network.getLinks().add(new Link(c, e));

        // 添加条件概率
        Table tableA = a.newDistribution().getTable();
        tableA.set(0.001, aState1);
        tableA.set(0.999, aState2);
        a.setDistribution(tableA);

        Table tableB = b.newDistribution().getTable();
        tableB.set(0.002, bState1);
        tableB.set(0.998, bState2);
        b.setDistribution(tableB);

        Table tableC = c.newDistribution().getTable();
        TableIterator iteratorC = new TableIterator(tableC, new Node[]{a, b, c});
        iteratorC.copyFrom(new double[]{0.95, 0.05, 0.94, 0.06, 0.29, 0.71, 0.001, 0.999});
        c.setDistribution(tableC);

        Table tableD = d.newDistribution().getTable();
        TableIterator iteratorD = new TableIterator(tableD, new Node[]{c, d});
        iteratorD.copyFrom(new double[]{0.90, 0.10, 0.05, 0.95});
        d.setDistribution(tableD);

        Table tableE = e.newDistribution().getTable();
        TableIterator iteratorE = new TableIterator(tableE, new Node[]{c, e});
        iteratorE.copyFrom(new double[]{0.70, 0.30, 0.01, 0.99});
        e.setDistribution(tableE);

        // network.save("测试贝叶斯网络.bayes");
        InferenceFactory factory = new RelevanceTreeInferenceFactory();
        Inference inference = factory.createInferenceEngine(network);
        QueryOptions queryOptions = factory.createQueryOptions();
        QueryOutput queryOutput = factory.createQueryOutput();

        // 设置证据节点，即该状态概率为100%
        inference.getEvidence().setState(cState1);

        Table queryA = new Table(a);
        inference.getQueryDistributions().add(queryA);

        Table queryB = new Table(b);
        inference.getQueryDistributions().add(queryB);

        Table queryC = new Table(c);
        inference.getQueryDistributions().add(queryC);

        Table queryD = new Table(d);
        inference.getQueryDistributions().add(queryD);

        Table queryE = new Table(e);

        inference.getQueryDistributions().add(queryE);

        inference.query(queryOptions, queryOutput);

        System.out.println(queryA.get(aState1));
        System.out.println(queryA.get(aState2));

        System.out.println(queryB.get(bState1));
        System.out.println(queryB.get(bState2));

        System.out.println(queryC.get(cState1));
        System.out.println(queryC.get(cState2));

        System.out.println(queryD.get(dState1));
        System.out.println(queryD.get(dState2));

        System.out.println(queryE.get(eState1));
        System.out.println(queryE.get(eState2));
    }

    public static void main(String[] args) throws XMLStreamException, IOException, InconsistentEvidenceException {
        testExample();
        // alarmExample();
    }
}
