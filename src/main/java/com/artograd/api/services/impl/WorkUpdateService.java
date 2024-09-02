package com.artograd.api.services.impl;

import com.artograd.api.model.WorkUpdate;
import com.artograd.api.repositories.WorkUpdateRepository;
import com.artograd.api.services.IWorkUpdateService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkUpdateService implements IWorkUpdateService {

  @Autowired
  private WorkUpdateRepository workUpdateRepository;

  @Override
  public WorkUpdate createWorkUpdate(WorkUpdate workUpdate) {
    return workUpdateRepository.save(workUpdate);
  }

  @Override
  public Optional<WorkUpdate> getWorkUpdateById(String id) {
    return workUpdateRepository.findById(id);
  }

  @Override
  public void deleteWorkUpdateById(String id) {
    workUpdateRepository.deleteById(id);
  }

  @Override
  public List<WorkUpdate> getWorkUpdatesByArtObjectId(String artObjectId) {
    return workUpdateRepository.findByArtObjectIdOrderByDateDesc(artObjectId);
  }
}
