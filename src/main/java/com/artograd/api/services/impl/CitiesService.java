package com.artograd.api.services.impl;

import com.artograd.api.model.City;
import com.artograd.api.repositories.CitiesRepository;
import com.artograd.api.services.ICitiesService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CitiesService implements ICitiesService {

  @Autowired private CitiesRepository cityRepository;

  @Override
  public List<City> getAllCities() {
    return cityRepository.findAll();
  }
}
