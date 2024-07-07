package com.itjing.ai.bayes.domain;

/**
 * 贝叶斯-连线
 *
 * @author lijing
 * @date 2024-07-03
 */
public class BayesLink {

	/**
	 * 开始节点
	 */
	private Integer from;

	/**
	 * 结束节点
	 */
	private Integer to;

	public BayesLink() {
	}

	public BayesLink(Integer from, Integer to) {
		this.from = from;
		this.to = to;
	}

	public Integer getFrom() {
		return from;
	}

	public void setFrom(Integer from) {
		this.from = from;
	}

	public Integer getTo() {
		return to;
	}

	public void setTo(Integer to) {
		this.to = to;
	}

	@Override
	public String toString() {
		return "BayesLink{" + "from=" + from + ", to=" + to + '}';
	}

}
