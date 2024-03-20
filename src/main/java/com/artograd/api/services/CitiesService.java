package com.artograd.api.services;

import com.artograd.api.model.City;
import com.artograd.api.repositories.CitiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CitiesService {
	
	@Autowired
    private CitiesRepository cityRepository;

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }
}
