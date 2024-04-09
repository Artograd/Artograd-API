package com.artograd.api.controllers;

import com.artograd.api.model.City;
import com.artograd.api.services.ICitiesService;
import com.artograd.api.utils.CommonUtils;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cities")
@AllArgsConstructor
public class CitiesController {

  private ICitiesService citiesService;

  @SuppressWarnings("unchecked")
  @GetMapping
  public ResponseEntity<List<City>> getAllCities() {
    List<City> cities = citiesService.getAllCities();
    return (ResponseEntity<List<City>>) CommonUtils.addCacheHeader(cities, 60);
  }
}
