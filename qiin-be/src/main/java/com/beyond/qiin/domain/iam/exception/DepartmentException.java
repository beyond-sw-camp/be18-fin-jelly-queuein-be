package com.beyond.qiin.domain.iam.exception;

import com.beyond.qiin.common.exception.BaseException;
import com.beyond.qiin.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class DepartmentException extends BaseException {

  public DepartmentException(ErrorCode code) {
    super(code);
  }

  // 부서 에러 정의
  public static DepartmentException notFound() {
    return new DepartmentException(DepartmentErrorCode.DEPARTMENT_NOT_FOUND);
  }

  public static DepartmentException duplicateName() {
    return new DepartmentException(DepartmentErrorCode.DEPARTMENT_DUPLICATE_NAME);
  }

  // ErrorCode 내부에 정의
  @Getter
  public enum DepartmentErrorCode implements ErrorCode {
    DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "DEPARTMENT_NOT_FOUND", "부서를 찾을 수 없습니다."),
    DEPARTMENT_DUPLICATE_NAME(HttpStatus.CONFLICT, "DEPARTMENT_DUPLICATE_NAME", "이미 존재하는 부서명입니다.");

    private final HttpStatus status;
    private final String error;
    private final String message;

    DepartmentErrorCode(final HttpStatus status, final String error, final String message) {
      this.status = status;
      this.error = error;
      this.message = message;
    }
  }
}
