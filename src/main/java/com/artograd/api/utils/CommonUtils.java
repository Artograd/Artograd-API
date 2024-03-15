package com.artograd.api.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public class CommonUtils {

	public static ResponseEntity<?> addCacheHeader(Object result, int seconds ) {
    	HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl("max-age="+seconds);
        
        return ResponseEntity.ok().headers(headers).body(result);
    }
}
