package com.itjing.ai.bayes.domain;

import java.util.List;

/**
 * 贝叶斯中节点的 Conditional Probability Table（条件概率表）
 *
 * @author lijing
 * @date 2024-07-04
 */
public class BayesNodeCPT {

    /**
     * 当前节点的索引值
     */
    private Integer index;

    /**
     * 条件概率表
     */
    private CPT cpt;

    /**
     * 当前节点的状态列表（如果数据不为空，则为先验概率）
     */
    private List<StateVal> stateVals;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public CPT getCpt() {
        return cpt;
    }

    public void setCpt(CPT cpt) {
        this.cpt = cpt;
    }

    public List<StateVal> getStateVals() {
        return stateVals;
    }

    public void setStateVals(List<StateVal> stateVals) {
        this.stateVals = stateVals;
    }

    @Override
    public String toString() {
        return "BayesNodeCPT{" +
                "index=" + index +
                ", cpt=" + cpt +
                ", stateVals=" + stateVals +
                '}';
    }

    /**
     * 条件概率表
     *
     * @author xiaojingge
     * @date 2024/07/04
     */
    public static class CPT {

        /**
         * 父节点结合
         */
        private List<BayesNode> fatherNodes;

        /**
         * 父节点和当前节点的组合列表
         */
        private List<OneCombin> combinList;

        public List<BayesNode> getFatherNodes() {
            return fatherNodes;
        }

        public void setFatherNodes(List<BayesNode> fatherNodes) {
            this.fatherNodes = fatherNodes;
        }

        public List<OneCombin> getCombinList() {
            return combinList;
        }

        public void setCombinList(List<OneCombin> combinList) {
            this.combinList = combinList;
        }

        @Override
        public String toString() {
            return "CPT{" +
                    "fatherNodes=" + fatherNodes +
                    ", combinList=" + combinList +
                    '}';
        }
    }

    /**
     * 状态值实体
     *
     * @author xiaojingge
     * @date 2024/07/04
     */
    public static class StateVal {

        /**
         * 状态名称
         */
        private String name;

        /**
         * 状态值
         */
        private Double value;

        /**
         * 所属节点索引
         */
        private Integer nodeIndex;

        public StateVal() {
        }

        public StateVal(String name, Double value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public Integer getNodeIndex() {
            return nodeIndex;
        }

        public void setNodeIndex(Integer nodeIndex) {
            this.nodeIndex = nodeIndex;
        }

        @Override
        public String toString() {
            return "StateVal{" +
                    "name='" + name + '\'' +
                    ", value=" + value +
                    ", nodeIndex=" + nodeIndex +
                    '}';
        }
    }

    /**
     * 一个组合
     *
     * @author xiaojingge
     * @date 2024/07/04
     */
    public static class OneCombin {

        /**
         * 父节点状态
         */
        private List<StateVal> parentsStates;

        /**
         * 当前节点的状态参数和值
         */
        private List<StateVal> currNode;

        public OneCombin() {
        }

        public OneCombin(List<StateVal> parentsStates, List<StateVal> currNode) {
            this.parentsStates = parentsStates;
            this.currNode = currNode;
        }

        public List<StateVal> getParentsStates() {
            return parentsStates;
        }

        public void setParentsStates(List<StateVal> parentsStates) {
            this.parentsStates = parentsStates;
        }

        public List<StateVal> getCurrNode() {
            return currNode;
        }

        public void setCurrNode(List<StateVal> currNode) {
            this.currNode = currNode;
        }

        @Override
        public String toString() {
            return "OneCombin{" +
                    "parentsStates=" + parentsStates +
                    ", currNode=" + currNode +
                    '}';
        }
    }
}
