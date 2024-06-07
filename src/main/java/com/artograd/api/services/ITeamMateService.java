package com.artograd.api.services;

import com.artograd.api.model.TeamMate;
import java.util.List;

public interface ITeamMateService {
  List<TeamMate> getActiveTeamMates();
}
