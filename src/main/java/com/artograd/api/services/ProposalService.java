package com.artograd.api.services;

import com.artograd.api.model.Proposal;
import com.artograd.api.model.Tender;
import com.artograd.api.model.User;
import com.artograd.api.model.UserAttribute;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProposalService {

    @Autowired
    private TenderService tenderService;
    
    @Autowired
    private CognitoService cognitoService;
    
    public Proposal getProposal(String tenderId, String proposalId) {
    	Tender tender = tenderService.getTender(tenderId);
        if (tender == null) {
        	return null;
        }

        return tender.getProposals().stream()
                .filter(proposal -> proposal.getId().equals(proposalId))
                .findFirst()
                .orElse(null); // Proposal not found
    }

    public Proposal createProposal(String tenderId, Proposal proposal) {
    	Tender tender = tenderService.getTender(tenderId);
        if (tender == null) {
        	return null;
        }
        
        if (tender.getProposals() == null) {
            tender.setProposals(new ArrayList<>());
        }

        proposal.setId(generateProposalId()); 
        proposal.setCreatedAt(new Date());
        proposal.setModifiedAt(new Date());
        
        proposal = setOwnerData(proposal);
        
        tender.getProposals().add(proposal);
        
        tenderService.updateTender(tender, false);
        return proposal;
    }

    public Proposal updateProposal(String tenderId, String proposalId, Proposal updatedProposal) {
        Tender tender = tenderService.getTender(tenderId);
        if (tender == null) {
            return null; // Tender not found
        }

        List<Proposal> proposals = tender.getProposals();
        String existingUserName = null;
        int index = -1;
        for (int i = 0; i < proposals.size(); i++) {
            if (proposals.get(i).getId().equals(proposalId)) {
                index = i;
                existingUserName = proposals.get(i).getOwnerId();
                break;
            }
        }

        if (index != -1) {
            updatedProposal.setId(proposalId); // Preserve the original ID
            updatedProposal.setModifiedAt(new Date()); // Set the modification date
            
            updatedProposal.setOwnerId( existingUserName );
            
            updatedProposal = setOwnerData(updatedProposal);
            
            proposals.set(index, updatedProposal); // Replace the proposal in the list
            tenderService.updateTender(tender, false); // Save the updated tender
            return updatedProposal;
        }

        return null; // Proposal not found
    }


    public boolean deleteProposal(String tenderId, String proposalId) {
    	Tender tender = tenderService.getTender(tenderId);
        if (tender == null) {
        	return false;
        }

        List<Proposal> updatedProposals = tender.getProposals().stream()
                .filter(proposal -> !proposal.getId().equals(proposalId))
                .collect(Collectors.toList());

        if (tender.getProposals().size() == updatedProposals.size()) {
            // No proposal was removed, indicating it wasn't found
            return false;
        }

        tender.setProposals(updatedProposals);
        
        tenderService.updateTender(tender, false);
        return true;
    }

    private String generateProposalId() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
    
    private Proposal setOwnerData(Proposal proposal) {
    	String username = proposal.getOwnerId();
    	if (StringUtils.isNotBlank(username)) {
    		User user = cognitoService.getUserByUsername(username);
    		if (user != null ) {
    			List<UserAttribute> attributes = user.getAttributes();
    			if ( attributes != null ) {
    				String name = "";
    				for (UserAttribute userAttribute : attributes) {
        				if ( userAttribute.getName().equals("picture") ) {
        					proposal.setOwnerPicture( userAttribute.getValue() );
        				}
        				if ( userAttribute.getName().equals("given_name") ) {
        					name = userAttribute.getValue() + name;
        				}
        				if ( userAttribute.getName().equals("family_name") ) {
        					name = name + " " + userAttribute.getValue();
        				}
        				if ( userAttribute.getName().equals("custom:organization") ) {
        					proposal.setOwnerOrg( userAttribute.getValue() );
        				}
        			}
    				
    				proposal.setOwnerName( name.trim() );
    			}
    		}
    		
    	}
    	return proposal;
    }
}
