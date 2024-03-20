package com.artograd.api.services;

import com.artograd.api.model.Tender;
import com.artograd.api.model.User;
import com.artograd.api.model.UserAttribute;
import com.artograd.api.repositories.TenderRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TenderService {

	@Autowired
    private TenderRepository tenderRepository;
	
	@Autowired
    private CognitoService cognitoService;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public Tender createTender(Tender tender) {
		tender.setModifiedAt( new Date() );
		tender.setCreatedAt( tender.getModifiedAt() );
		tender = setOwnerData(tender);
        return tenderRepository.save(tender);
    }

    public Tender getTender(String id) {
        return tenderRepository.findById(id).orElse(null);
    }

    public Tender updateTender(Tender tender) {
    	tender.setModifiedAt( new Date() );
    	tender = setOwnerData(tender);
    	return tenderRepository.save(tender);
    }

    public void deleteTender(String id) {
        tenderRepository.deleteById(id);
    }

    public List<Tender> searchTenders(
    		String title, List<String> locationLeafIds, List<String> statuses, 
    		String ownerId, int page, int size, String sortBy, String sortOrder) {
    	
    	final Query query = new Query();
        final List<Criteria> criteria = new ArrayList<>();

        if ( title != null && !title.isBlank() ) {
        	criteria.add(Criteria.where("title").regex(title, "i"));
        }
        
        if ( ownerId != null && !ownerId.isBlank() ) {
        	criteria.add(Criteria.where("ownerId").is(ownerId));
        }
        
        if (locationLeafIds != null && !locationLeafIds.isEmpty()) {
            criteria.add(Criteria.where("locationLeafId").in(locationLeafIds));
        }

        if (statuses != null && !statuses.isEmpty()) {
            criteria.add(Criteria.where("status").in(statuses));
        }

        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
        
        final Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        query.with(pageable);

        return mongoTemplate.find(query, Tender.class);
    }
    
    public long getCountByOwnerIdAndStatusIn(String ownerId, List<String> statuses) {
            Query query = new Query();
            query.addCriteria(Criteria.where("ownerId").is(ownerId));
            
            if (statuses != null && !statuses.isEmpty()) {
                query.addCriteria(Criteria.where("status").in(statuses));
            }

            return mongoTemplate.count(query, Tender.class);
    }
    
    private Tender setOwnerData(Tender tender) {
    	String username = tender.getOwnerId();
    	if (StringUtils.isNotBlank(username)) {
    		User user = cognitoService.getUserByUsername(username);
    		if (user != null ) {
    			List<UserAttribute> attributes = user.getAttributes();
    			if ( attributes != null ) {
    				String name = "";
    				for (UserAttribute userAttribute : attributes) {
        				if ( userAttribute.getName().equals("picture") ) {
        					tender.setOwnerPicture( userAttribute.getValue() );
        				}
        				if ( userAttribute.getName().equals("given_name") ) {
        					name = userAttribute.getValue() + name;
        				}
        				if ( userAttribute.getName().equals("family_name") ) {
        					name = name + " " + userAttribute.getValue();
        				}
        				if ( userAttribute.getName().equals("custom:organization") ) {
        					tender.setOrganization( userAttribute.getValue() );
        				}
        			}
    				
    				tender.setOwnerName( name.trim() );
    			}
    		}
    		
    	}
    	return tender;
    }
}
