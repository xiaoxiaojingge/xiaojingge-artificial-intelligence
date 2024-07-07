package com.itjing.ai.bayes.enums;

/**
 * 贝叶斯提示信息类型
 *
 * @author lijing
 * @date 2024-07-04
 */
public enum BayesTipInfoTypeEnum {

	NORMAL(1, "普通信息"),

	WARN(2, "警告信息"),

	ERROR(3, "错误信息"),

	SUCCESS(4, "成功信息");

	private Integer code;

	private String message;

	BayesTipInfoTypeEnum(Integer code, String message) {
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
