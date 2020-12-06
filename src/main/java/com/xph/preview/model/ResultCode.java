package com.xph.preview.model;

/**
 * 错误码
 * @author xiaoph
 *
 */
public enum ResultCode {

	SUCCESS(200, "操作成功"), TOKEN_ERROR_(2, "token验证失败"), ERROR(3, "当前服务器出错"), FAIL(
			4, "当前操作失败"), PARAM_ERROR(5, "参数错误");
	private int code;
	private String message;

	ResultCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
