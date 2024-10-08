package com.itjing.ai.bayes.domain;

import com.itjing.ai.bayes.enums.BayesErrorModifierTypeEnum;
import com.itjing.ai.bayes.enums.BayesTipInfoTypeEnum;

/**
 * 贝叶斯提示信息
 *
 * @author lijing
 * @date 2024-07-04
 */
public class BayesTipInfo {

	/**
	 * 提示类型
	 */
	private BayesTipInfoTypeEnum type;

	/**
	 * 实体信息
	 */
	private String message;

	/**
	 * 错误修改类型
	 */
	private BayesErrorModifierTypeEnum modifierType;

	public BayesTipInfo() {
	}

	public BayesTipInfo(BayesTipInfoTypeEnum type, String message) {
		this.type = type;
		this.message = message;
	}

	public BayesTipInfoTypeEnum getType() {
		return type;
	}

	public void setType(BayesTipInfoTypeEnum type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public BayesErrorModifierTypeEnum getModifierType() {
		return modifierType;
	}

	public void setModifierType(BayesErrorModifierTypeEnum modifierType) {
		this.modifierType = modifierType;
	}

	@Override
	public String toString() {
		return "BayesTipInfo{" + "type=" + type + ", message='" + message + '\'' + ", modifierType=" + modifierType
				+ '}';
	}

}
