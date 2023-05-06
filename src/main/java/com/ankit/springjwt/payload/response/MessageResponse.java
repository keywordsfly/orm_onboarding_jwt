package com.ankit.springjwt.payload.response;

public class MessageResponse {
  private String message;
  
  private String code;

  public MessageResponse(String message,String code) {
    this.message = message;
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
