package com.artograd.api.controllers;

import com.artograd.api.model.City;
import com.artograd.api.services.CitiesService;
import com.artograd.api.utils.CommonUtils;
import com.artograd.api.utils.ResponseHandler;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cities")
public class CitiesController {

  @Autowired private CitiesService citiesService;

  @SuppressWarnings("unchecked")
  @GetMapping
  public ResponseEntity<Map<String, Object>> getAllCities() {
    List<City> cities = citiesService.getAllCities();
    HttpHeaders headers = CommonUtils.createHeaders(60);

    return ResponseHandler.generateResponse(cities, headers, HttpStatus.OK);
  }
}
