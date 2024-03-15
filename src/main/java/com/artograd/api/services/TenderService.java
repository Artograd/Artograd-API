package com.artograd.api.services;

import com.artograd.api.model.Tender;
import com.artograd.api.repositories.TenderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TenderService {

    @Autowired
    private TenderRepository tenderRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Tender createTender(Tender tender) {
        return tenderRepository.save(tender);
    }

    public Tender getTender(String id) {
        return tenderRepository.findById(id).orElse(null);
    }

    public Tender updateTender(Tender tender) {
        return tenderRepository.save(tender);
    }

    public void deleteTender(String id) {
        tenderRepository.deleteById(id);
    }

    public List<Tender> searchTenders(String title, List<String> locations, List<String> statuses, String owner) {
        final Query query = new Query();
        final List<Criteria> criteria = new ArrayList<>();

        if (title != null && !title.isBlank()) {
            criteria.add(Criteria.where("title").regex(title, "i"));
        }

        if (owner != null && !owner.isBlank()) {
            criteria.add(Criteria.where("owner").is(owner));
        }

        if (locations != null && !locations.isEmpty()) {
            criteria.add(Criteria.where("location").in(locations));
        }

        if (statuses != null && !statuses.isEmpty()) {
            criteria.add(Criteria.where("status").in(statuses));
        }

        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }

        return mongoTemplate.find(query, Tender.class);
    }
}
