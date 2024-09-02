package com.artograd.api.services;

import com.artograd.api.model.WorkUpdate;
import java.util.List;
import java.util.Optional;

public interface IWorkUpdateService {

  WorkUpdate createWorkUpdate(WorkUpdate workUpdate);

  Optional<WorkUpdate> getWorkUpdateById(String id);

  void deleteWorkUpdateById(String id);

  List<WorkUpdate> getWorkUpdatesByArtObjectId(String artObjectId);
}
