package com.xph.preview.model;

import java.io.Serializable;
import java.util.Collections;

/**
 * 类或方法的功能描述
 *
 * @Author: xph
 */
public class BaseReturnVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected int resCode; // 结果码
	protected String resDes; // 结果描述
	protected Object data;

	public int getResCode() {
		return resCode;
	}

	public void setResCode(int resCode) {
		this.resCode = resCode;
	}

	public String getResDes() {
		return resDes;
	}

	public void setResDes(String resDes) {
		this.resDes = resDes;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public BaseReturnVO() {
	}

	public BaseReturnVO(int code, String msg) {
		this.resCode = code;
		this.resDes = msg;
		this.data = Collections.EMPTY_LIST;

	}

	public BaseReturnVO(int code, String msg, Object data) {
		this.resCode = code;
		this.resDes = msg;
		this.data = data;

	}

	public BaseReturnVO(int code, Exception e) {
		this.resCode = code;
		this.resDes = e.getMessage();
		this.data = "";

	}

	public BaseReturnVO(int code, String msg, Exception e) {
		this.resCode = code;
		this.resDes = msg;
		this.data = "";
	}

	public BaseReturnVO(Object data) {
		this.resCode = ResultCode.SUCCESS.getCode();
		this.resDes = "SUCCESS";
		this.data = data;
	}

	public static BaseReturnVO success() {
		BaseReturnVO vo=new BaseReturnVO();
		vo.setResCode(ResultCode.SUCCESS.getCode());
		vo.setResDes("SUCCESS");
		return vo;
	}
	
	public static BaseReturnVO error() {
		BaseReturnVO vo=new BaseReturnVO();
		vo.setResCode(ResultCode.ERROR.getCode());
		vo.setResDes("ERROR");
		return vo;
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		return sb.append("{resCode:" + this.resCode)
				.append(",resDes:" + resDes).append(",data:" + data + "}")
				.toString();
	}
}
