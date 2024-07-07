package com.itjing.ai.bayes.domain;

import java.util.List;

/**
 * 贝叶斯-节点
 *
 * @author lijing
 * @date 2024-07-03
 */
public class BayesNode {

	/**
	 * 节点标识
	 */
	private Integer index;

	/**
	 * 是否有父节点
	 */
	private Boolean haveFather;

	/**
	 * 节点类型
	 */
	private Integer type;

	/**
	 * 节点文本
	 */
	private String text;

	/**
	 * 状态名称列表
	 */
	private List<String> stateNameList;

	public BayesNode() {
	}

	public BayesNode(Integer index, Boolean haveFather, Integer type, String text, List<String> stateNameList) {
		this.index = index;
		this.haveFather = haveFather;
		this.type = type;
		this.text = text;
		this.stateNameList = stateNameList;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Boolean getHaveFather() {
		return haveFather;
	}

	public void setHaveFather(Boolean haveFather) {
		this.haveFather = haveFather;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getStateNameList() {
		return stateNameList;
	}

	public void setStateNameList(List<String> stateNameList) {
		this.stateNameList = stateNameList;
	}

	@Override
	public String toString() {
		return "BayesNode{" + "index=" + index + ", haveFather=" + haveFather + ", type=" + type + ", text='" + text
				+ '\'' + ", stateNameList='" + stateNameList + '\'' + '}';
	}

}
