package com.itjing.ai.bayes.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.itjing.ai.bayes.domain.BayesLink;
import com.itjing.ai.bayes.domain.BayesNode;
import com.itjing.ai.bayes.domain.BayesNodeCPT;
import com.itjing.ai.bayes.domain.BayesNodeEndValTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 分析上下文对象
 *
 * @author lijing
 * @date 2024-07-05
 */
public class AnalysisContext {

	/**
	 * 节点列表
	 */
	private List<BayesNode> nodes = Lists.newArrayList();

	/**
	 * 链接列表
	 */
	private List<BayesLink> links = Lists.newArrayList();

	/**
	 * 节点CPT列表
	 */
	private List<BayesNodeCPT> nodeCPTs = Lists.newArrayList();

	/**
	 * 构建从节点索引到节点对象的映射
	 */
	private Map<Integer, BayesNode> nodeMap = Maps.newHashMap();

	/**
	 * 构建节点CPT的映射
	 */
	private Map<Integer, BayesNodeCPT> nodeCPTMap = Maps.newHashMap();

	/**
	 * 构建链接关系，方便查询每个节点的父节点
	 */
	private Map<Integer, Set<Integer>> nodeParentMap = Maps.newHashMap();

	/**
	 * 构建链接关系，方便查询每个节点的子节点
	 */
	private Map<Integer, Set<Integer>> nodeChildrenMap = Maps.newHashMap();

	private List<BayesNodeEndValTable> endValTableList = Lists.newArrayList();

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

	public List<BayesNodeCPT> getNodeCPTs() {
		return nodeCPTs;
	}

	public void setNodeCPTs(List<BayesNodeCPT> nodeCPTs) {
		this.nodeCPTs = nodeCPTs;
	}

	public Map<Integer, BayesNode> getNodeMap() {
		return nodeMap;
	}

	public void setNodeMap(Map<Integer, BayesNode> nodeMap) {
		this.nodeMap = nodeMap;
	}

	public Map<Integer, BayesNodeCPT> getNodeCPTMap() {
		return nodeCPTMap;
	}

	public void setNodeCPTMap(Map<Integer, BayesNodeCPT> nodeCPTMap) {
		this.nodeCPTMap = nodeCPTMap;
	}

	public Map<Integer, Set<Integer>> getNodeParentMap() {
		return nodeParentMap;
	}

	public void setNodeParentMap(Map<Integer, Set<Integer>> nodeParentMap) {
		this.nodeParentMap = nodeParentMap;
	}

	public Map<Integer, Set<Integer>> getNodeChildrenMap() {
		return nodeChildrenMap;
	}

	public void setNodeChildrenMap(Map<Integer, Set<Integer>> nodeChildrenMap) {
		this.nodeChildrenMap = nodeChildrenMap;
	}

	public List<BayesNodeEndValTable> getEndValTableList() {
		return endValTableList;
	}

	public void setEndValTableList(List<BayesNodeEndValTable> endValTableList) {
		this.endValTableList = endValTableList;
	}

}
