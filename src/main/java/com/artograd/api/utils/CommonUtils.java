package com.artograd.api.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class CommonUtils {

  /**
   * Adds cache headers to the response entity.
   *
   * @param result The object to be returned as the response body.
   * @param seconds The number of seconds to set as the max-age cache control value.
   * @return A response entity with cache headers set.
   */
  public static ResponseEntity<?> addCacheHeader(Object result, int seconds) {
    HttpHeaders headers = new HttpHeaders();
    headers.setCacheControl("max-age=" + seconds);

    return ResponseEntity.ok().headers(headers).body(result);
  }

  /**
   * Parses the token from the Authorization header in the HttpServletRequest.
   *
   * @param request The HttpServletRequest object.
   * @return The parsed token extracted from the Authorization header, or null if no valid token
   *         is found.
   */
  public static String parseToken(HttpServletRequest request) {
    String authorizationHeader = request.getHeader("Authorization");
    String token = null;

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      token = authorizationHeader.substring(7); // Extract token without "Bearer "
    }

    return token;
  }
}
