package com.artograd.api.services;

import com.artograd.api.model.Tender;
import com.artograd.api.model.TenderSearchCriteria;
import java.util.List;
import java.util.Optional;

public interface ITenderService {
    Tender createTender(Tender tender);
    Optional<Tender> getTender(String id);
    Optional<Tender> updateTender(Tender tender);
    void deleteTender(String id);
    List<Tender> searchTenders(TenderSearchCriteria criteria);
    long getCountByOwnerIdAndStatusIn(String ownerId, List<String> statuses);
    boolean isTenderOwner(String tenderId, String username);
}
