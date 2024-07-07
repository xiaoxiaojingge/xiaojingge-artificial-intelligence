package com.itjing.ai.bayes.domain;

import java.util.List;

/**
 * 贝叶斯最终计算概率
 *
 * @author lijing
 * @date 2024-07-06
 */
public class BayesNodeEndValTable {

	private Integer nodeIndex;

	private List<Double> endValTable;

	public Integer getNodeIndex() {
		return nodeIndex;
	}

	public void setNodeIndex(Integer nodeIndex) {
		this.nodeIndex = nodeIndex;
	}

	public List<Double> getEndValTable() {
		return endValTable;
	}

	public void setEndValTable(List<Double> endValTable) {
		this.endValTable = endValTable;
	}

	@Override
	public String toString() {
		return "BayesNodeEndValTable{" + "nodeIndex=" + nodeIndex + ", endValTable=" + endValTable + '}';
	}

}