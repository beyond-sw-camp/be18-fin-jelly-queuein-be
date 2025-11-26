package com.beyond.qiin.domain.iam.exception;

import com.beyond.qiin.common.exception.BaseException;
import com.beyond.qiin.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class UserException extends BaseException {

  private UserException(final UserErrorCode code) {
    super(code);
  }

  // 유저 에러 정의
  public static UserException userNotFound() {
    return new UserException(UserErrorCode.USER_NOT_FOUND);
  }

  public static UserException userAlreadyExists() {
    return new UserException(UserErrorCode.USER_ALREADY_EXISTS);
  }

  public static UserException invalidPassword() {
    return new UserException(UserErrorCode.INVALID_PASSWORD);
  }

  public static UserException passwordExpired() {
    return new UserException(UserErrorCode.PASSWORD_EXPIRED);
  }

  public static UserException passwordChangeNotAllowed() {
    return new UserException(UserErrorCode.PASSWORD_CHANGE_NOT_ALLOWED);
  }

  // ErrorCode 내부에 정의
  @Getter
  public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "해당 사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(
        HttpStatus.CONFLICT,
        "USER_ALREADY_EXISTS",
        "이미 존재하는 사용자입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED,
        "INVALID_PASSWORD",
        "비밀번호가 일치하지 않습니다."),
    PASSWORD_EXPIRED(HttpStatus.UNAUTHORIZED,
        "PASSWORD_EXPIRED",
        "임시 비밀번호 사용자는 비밀번호를 변경해야 합니다."),
    PASSWORD_CHANGE_NOT_ALLOWED(
        HttpStatus.FORBIDDEN,
        "PASSWORD_CHANGE_NOT_ALLOWED",
        "해당 사용자는 임시 비밀번호 변경을 수행할 수 없습니다."),
    USER_GONE(
        HttpStatus.GONE,
        "USER_GONE",
        "이미 삭제된 사용자입니다."
    );

    private final HttpStatus status;
    private final String error;
    private final String message;

    UserErrorCode(final HttpStatus status, final String error, final String message) {
      this.status = status;
      this.error = error;
      this.message = message;
    }
  }
}
