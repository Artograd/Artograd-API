package com.artograd.api.services.impl;

import com.artograd.api.model.TeamMate;
import com.artograd.api.repositories.TeamMateRepository;
import com.artograd.api.services.ITeamMateService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TeamMateService implements ITeamMateService {

  private final TeamMateRepository teamMateRepository;

  @Autowired
  public TeamMateService(TeamMateRepository teamMateRepository) {
    this.teamMateRepository = teamMateRepository;
  }

  public List<TeamMate> getActiveTeamMates() {
    return teamMateRepository.findByActiveTrue();
  }
}
