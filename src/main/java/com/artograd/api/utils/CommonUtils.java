package com.artograd.api.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;

@UtilityClass
public class CommonUtils {

  public static HttpHeaders createHeaders(int cacheAgeSeconds) {
    HttpHeaders headers = new HttpHeaders();
    headers.setCacheControl("max-age=" + cacheAgeSeconds);
    return headers;
  }

  public static String parseToken(HttpServletRequest request) {
    String authorizationHeader = request.getHeader("Authorization");
    String token = null;

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      token = authorizationHeader.substring(7); // Extract token without "Bearer "
    }

    return token;
  }
}
