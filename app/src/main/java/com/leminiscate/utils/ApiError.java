package com.leminiscate.utils;

public class ApiError {

  public int code;
  private String message;

  ApiError() {
  }

  ApiError(int code, String message) {

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
