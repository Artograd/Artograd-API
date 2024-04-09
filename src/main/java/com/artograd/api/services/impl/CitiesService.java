package com.artograd.api.services.impl;

import com.artograd.api.model.City;
import com.artograd.api.repositories.CitiesRepository;
import com.artograd.api.services.ICitiesService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CitiesService implements ICitiesService {

  private CitiesRepository cityRepository;

  @Override
  public List<City> getAllCities() {
    return cityRepository.findAll();
  }
}
