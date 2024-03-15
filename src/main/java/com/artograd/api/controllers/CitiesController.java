package com.artograd.api.controllers;

import com.artograd.api.model.City;
import com.artograd.api.services.CitiesService;
import com.artograd.api.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cities")
public class CitiesController {

    @Autowired
    private CitiesService citiesService;

    @SuppressWarnings("unchecked")
    @GetMapping
    public ResponseEntity<City> getAllCountries() {
        List<City> cities = citiesService.getAllCities();
        return (ResponseEntity<City>) CommonUtils.addCacheHeader(cities, 60);
    }

}

