package com.itjing.ai.bayes.enums;

/**
 * 贝叶斯错误修改类型
 *
 * @author lijing
 * @date 2024-07-04
 */
public enum BayesErrorModifierTypeEnum {

	/**
	 * 必须修改
	 */
	MUST_MODIFY(1, "必须修改"),

	/**
	 * 建议修改
	 */
	SUGGEST_MODIFY(2, "建议修改");

	private Integer code;

	private String message;

	BayesErrorModifierTypeEnum(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
