package com.artograd.api.services;

import com.artograd.api.model.Proposal;
import com.artograd.api.model.User;
import com.artograd.api.model.UserAttribute;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProposalService {

	@Autowired
    private TenderService tenderService;

    @Autowired
    private CognitoService cognitoService;
    
    /**
     * Retrieves a proposal by its ID within a specific tender.
     *
     * @param tenderId    The ID of the tender.
     * @param proposalId  The ID of the proposal to find.
     * @return An Optional containing the found proposal or empty if not found.
     */
    public Optional<Proposal> getProposal(String tenderId, String proposalId) {
        return tenderService.getTender(tenderId)
            .flatMap(tender -> tender.getProposals().stream()
                .filter(proposal -> proposal.getId().equals(proposalId))
                .findFirst());
    }

    /**
     * Creates a new proposal in the specified tender.
     *
     * @param tenderId  The ID of the tender where the proposal will be added.
     * @param proposal  The proposal object to add.
     * @return An Optional containing the created proposal or empty if the tender doesn't exist.
     */
    public Optional<Proposal> createProposal(String tenderId, Proposal proposal) {
        return tenderService.getTender(tenderId)
            .map(tender -> {
                proposal.setId(generateProposalId());
                proposal.setCreatedAt(new Date());
                proposal.setModifiedAt(new Date());

                enrichProposalWithOwnerData(proposal);

                tender.getProposals().add(proposal);
                tenderService.updateTender(tender, false);
                return proposal;
            });
    }

    /**
     * Deletes a proposal from a tender.
     *
     * @param tenderId    The ID of the tender from which the proposal will be deleted.
     * @param proposalId  The ID of the proposal to delete.
     * @return true if the proposal was successfully deleted, false otherwise.
     */
    public boolean deleteProposal(String tenderId, String proposalId) {
        return tenderService.getTender(tenderId)
            .map(tender -> {
                boolean removed = tender.getProposals().removeIf(proposal -> proposal.getId().equals(proposalId));
                if (removed) {
                    tenderService.updateTender(tender, false);
                }
                return removed;
            }).orElse(false);
    }
    
    /**
     * Updates an existing proposal within a tender.
     *
     * @param tenderId         The ID of the tender containing the proposal.
     * @param proposalId       The ID of the proposal to update.
     * @param updatedProposal  The new proposal data to apply.
     * @return An Optional containing the updated proposal or empty if not found.
     */
    public Optional<Proposal> updateProposal(String tenderId, String proposalId, Proposal updatedProposal) {
        return tenderService.getTender(tenderId)
            .flatMap(tender -> tender.getProposals().stream()
                .filter(proposal -> proposal.getId().equals(proposalId))
                .findFirst()
                .map(proposal -> {
                    updateExistingProposal(proposal, updatedProposal);
                    tenderService.updateTender(tender, false);
                    return proposal;
                }));
    }

    private void updateExistingProposal(Proposal existingProposal, Proposal updatedProposal) {
    	updatedProposal.setId(existingProposal.getId());
    	updatedProposal.setCreatedAt(existingProposal.getCreatedAt()); // Preserve the original creation date
    	updatedProposal.setModifiedAt(new Date()); // Update the modification date
    	updatedProposal.setOwnerId(existingProposal.getOwnerId());
        
    	ModelMapper modelMapper = new ModelMapper();
        modelMapper.map(updatedProposal, existingProposal);

        enrichProposalWithOwnerData(existingProposal);
    }

    private void enrichProposalWithOwnerData(Proposal proposal) {
        if (StringUtils.isNotBlank(proposal.getOwnerId())) {
            cognitoService.getUserByUsername(proposal.getOwnerId())
	            .ifPresent(user -> {
	                proposal.setOwnerName(formatUserName(user));
	                proposal.setOwnerPicture(findUserAttribute(user, "picture"));
	                proposal.setOwnerOrg(findUserAttribute(user, "custom:organization"));
	            });
        }
    }

    private String formatUserName(User user) {
        String givenName = findUserAttribute(user, "given_name");
        String familyName = findUserAttribute(user, "family_name");
        return String.format("%s %s", givenName, familyName).trim();
    }

    private String findUserAttribute(User user, String attributeName) {
        return user.getAttributes().stream()
            .filter(attr -> attr.getName().equals(attributeName))
            .findFirst()
            .map(UserAttribute::getValue)
            .orElse("");
    }
    
    private String generateProposalId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
