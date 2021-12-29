package com.cbs.springboot.bean;

/**
 * 微服务交互状态
 * 
 * @author chengxt1202
 *
 */
public enum StatusEnum {

	// 业务和接口都成功
	SUCCESS(0, "接口调用成功，业务处理是上游预想的结果"),
	// 下游或者被调用方程序异常，上游应当重试
	ERROR(-1000, "系统异常上游应该重试"),
	// 返回结果不是调用方想要的结果不需要重试
	FAILED(-1, "业务失败上游不应该重试，联系下游解决逻辑错误"),
	
	
	//消息队列内容操作类型
	STREAM_DATA(1,"数据传输"),
	STREAM_ADD(2,"新增数据"),
	STREAM_UPDATE(3,"更新数据"),
	STREAM_DELETE(4,"删除数据");

	private StatusEnum(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	public boolean isSuccess() {
		return this.code == 0;
	}

	public boolean isNotSuccess() {
		return this.code != 0;
	}

	private Integer code;
	private String message;

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

	@Override
	public String toString() {
		return this.code.toString();
	}

}
