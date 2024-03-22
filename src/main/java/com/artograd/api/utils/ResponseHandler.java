package com.artograd.api.utils;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class ResponseHandler {

  private static final String SUCCESS = "success";
  private static final String MESSAGE = "message";
  private static final String DATA = "data";

  private ResponseHandler() {}

  public static ResponseEntity<Map<String, Object>> generateResponse(
      Object bodyObject, HttpHeaders headers, HttpStatusCode statusCode) {

    Map<String, Object> body = generateBody(bodyObject, statusCode, null);
    return new ResponseEntity<>(body, headers, statusCode);
  }

  public static ResponseEntity<Map<String, Object>> generateResponse(
      Object bodyObject, HttpStatusCode statusCode) {

    Map<String, Object> body = generateBody(bodyObject, statusCode, null);
    return new ResponseEntity<>(body, statusCode);
  }

  public static ResponseEntity<Map<String, Object>> generateResponse(
      Object bodyObject, HttpHeaders headers, HttpStatusCode statusCode, String message) {

    Map<String, Object> body = generateBody(bodyObject, statusCode, message);
    return new ResponseEntity<>(body, headers, statusCode);
  }

  public static ResponseEntity<Map<String, Object>> generateResponse(
      HttpHeaders headers, HttpStatusCode statusCode, String message) {

    Map<String, Object> body = generateBody(null, statusCode, message);
    return new ResponseEntity<>(body, headers, statusCode);
  }

  public static ResponseEntity<Map<String, Object>> generateResponse(
      HttpStatusCode statusCode, String message) {

    Map<String, Object> body = generateBody(null, statusCode, message);
    return new ResponseEntity<>(body, statusCode);
  }

  public static ResponseEntity<Map<String, Object>> generateResponse(HttpStatusCode statusCode) {

    Map<String, Object> body = generateBody(null, statusCode, null);
    return new ResponseEntity<>(body, statusCode);
  }

  private static Map<String, Object> generateBody(
      Object bodyObject, HttpStatusCode statusCode, String message) {
    Map<String, Object> body = new HashMap<>();

    boolean successStatusCode = statusCode.value() >= 200 && statusCode.value() < 300;
    body.put(SUCCESS, successStatusCode);

    if (successStatusCode && bodyObject != null) {
      body.put(DATA, bodyObject);
    }

    if (StringUtils.isNotBlank(message)) {
      body.put(MESSAGE, message);
    }

    return body;
  }
}
