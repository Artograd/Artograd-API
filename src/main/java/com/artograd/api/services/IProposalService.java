package com.artograd.api.services;

import com.artograd.api.model.Proposal;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface IProposalService {
    Optional<Proposal> getProposal(String tenderId, String proposalId);
    Optional<Proposal> createProposal(String tenderId, Proposal proposal, String realUserName);
    boolean deleteProposal(String tenderId, String proposalId);
    Optional<Proposal> updateProposal(String tenderId, String proposalId, Proposal updatedProposal);
    boolean isProposalOperationAllowed(String tenderId, String proposalId, HttpServletRequest request);
}
