package com.itjing.ai.bayes.domain;

import java.util.List;

/**
 * 贝叶斯网络实体
 *
 * @author lijing
 * @date 2024-07-04
 */
public class BayesNetwork {

    /**
     * 节点
     */
    private List<BayesNode> nodes;

    /**
     * 关系
     */
    private List<BayesLink> links;

    /**
     * 节点的条件概率表
     */
    private List<BayesNodeCPT> nodeCPTs;

    /**
     * 提示信息
     */
    private List<BayesTipInfo> tipInfos;

    public BayesNetwork() {
    }

    public BayesNetwork(List<BayesNode> nodes, List<BayesLink> links, List<BayesNodeCPT> nodeCPTs) {
        this.nodes = nodes;
        this.links = links;
        this.nodeCPTs = nodeCPTs;
    }

    public List<BayesNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<BayesNode> nodes) {
        this.nodes = nodes;
    }

    public List<BayesLink> getLinks() {
        return links;
    }

    public void setLinks(List<BayesLink> links) {
        this.links = links;
    }

    public List<BayesTipInfo> getTipInfos() {
        return tipInfos;
    }

    public void setTipInfos(List<BayesTipInfo> tipInfos) {
        this.tipInfos = tipInfos;
    }

    public List<BayesNodeCPT> getNodeCPTs() {
        return nodeCPTs;
    }

    public void setNodeCPTs(List<BayesNodeCPT> nodeCPTs) {
        this.nodeCPTs = nodeCPTs;
    }

    @Override
    public String toString() {
        return "BayesNetwork{" +
                "nodes=" + nodes +
                ", links=" + links +
                ", nodeCPTs=" + nodeCPTs +
                ", tipInfos=" + tipInfos +
                '}';
    }
}
