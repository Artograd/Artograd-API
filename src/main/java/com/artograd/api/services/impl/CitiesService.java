package com.artograd.api.services.impl;

import com.artograd.api.model.City;
import com.artograd.api.repositories.CitiesRepository;
import com.artograd.api.services.ICitiesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CitiesService implements ICitiesService {
	
    @Autowired
    private CitiesRepository cityRepository;

    @Override
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }
}