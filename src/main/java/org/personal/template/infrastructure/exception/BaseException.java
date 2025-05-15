package org.personal.template.infrastructure.exception;

import org.personal.template.infrastructure.response.Code;
import static org.personal.template.infrastructure.response.Code.INTERNAL_SERVER_ERROR;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

	private final Code errorCode;

	// 기본 생성자에서는 일반적인 에러 코드를 사용
	public BaseException() {
		super(INTERNAL_SERVER_ERROR.getMessage());
		this.errorCode = INTERNAL_SERVER_ERROR;
	}

	// 에러 메시지를 받는 생성자
	public BaseException(String message) {
		super(message);
		this.errorCode = INTERNAL_SERVER_ERROR;
	}

	// 에러 메시지와 원인을 받는 생성자
	public BaseException(String message, Throwable cause) {
		super(message, cause);
		this.errorCode = INTERNAL_SERVER_ERROR;
	}

	// 원인만을 받는 생성자
	public BaseException(Throwable cause) {
		super(cause);
		this.errorCode = INTERNAL_SERVER_ERROR;
	}

	// 에러 코드를 지정하는 생성자
	public BaseException(Code errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	// 에러 코드와 메시지를 받는 생성자
	public BaseException(Code errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	// 에러 코드, 메시지, 원인을 받는 생성자
	public BaseException(Code errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	// 에러 코드와 원인을 받는 생성자
	public BaseException(Code errorCode, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
	}
}
