package com.artograd.api.controllers;

import com.artograd.api.model.Tender;
import com.artograd.api.services.TenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/tenders")
public class TenderController {

    @Autowired
    private TenderService tenderService;

    @PostMapping
    public ResponseEntity<Tender> createTender(@RequestBody Tender tender) {
        Tender createdTender = tenderService.createTender(tender);
        return new ResponseEntity<>(createdTender, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tender> getTender(@PathVariable String id) {
        Tender tender = tenderService.getTender(id);
        if (tender != null) {
            return new ResponseEntity<>(tender, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tender> updateTender(@PathVariable String id, @RequestBody Tender tender) {
        Tender updatedTender = tenderService.updateTender(tender);
        if (updatedTender != null) {
            return new ResponseEntity<>(updatedTender, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTender(@PathVariable String id) {
        tenderService.deleteTender(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<Tender>> searchTenders(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> locations,
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) String owner) {
        List<Tender> tenders = tenderService.searchTenders(title, 
        		locations != null ? locations : new ArrayList<>(), 
        		statuses  != null ? statuses  : new ArrayList<>(),
        owner);
        return new ResponseEntity<>(tenders, HttpStatus.OK);
    }

}
