package com.artograd.api.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class CommonUtils {

  /**
   * Adds a cache control header to the response entity.
   *
   * @param result  the object to be returned in the response body
   * @param seconds the number of seconds to set in the cache control header
   * @return a ResponseEntity with the cache control header and the given result as the response
   *         body
   */
  public static ResponseEntity<?> addCacheHeader(Object result, int seconds) {
    HttpHeaders headers = new HttpHeaders();
    headers.setCacheControl("max-age=" + seconds);

    return ResponseEntity.ok().headers(headers).body(result);
  }

  /**
   * Parses the token from the Authorization header of the given HttpServletRequest.
   *
   * @param request the HttpServletRequest object
   * @return the parsed token or null if not found
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
