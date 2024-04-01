package com.artograd.api.services;

import java.util.List;
import java.util.Optional;

import com.artograd.api.model.ArtObject;
import com.artograd.api.model.ArtObjectSearchCriteria;

public interface IArtObjectService {

	Optional<ArtObject> createArtObject(String tenderId, String winnerProposalId);

	Optional<ArtObject> updateArtObject(String id, ArtObject artObject);

	void deleteArtObject(String id);

	Optional<ArtObject> getArtObject(String id);

	long countArtObjects(List<String> statuses, String userId);

	List<ArtObject> searchArtObjects(ArtObjectSearchCriteria artObjectSearchCriteria);

	boolean isArtObjectOwner(String objectId, String username);

}
