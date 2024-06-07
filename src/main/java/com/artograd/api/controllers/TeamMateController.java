package com.artograd.api.controllers;

import com.artograd.api.model.TeamMate;
import com.artograd.api.services.ITeamMateService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/team")
public class TeamMateController {

  private final ITeamMateService teamMateService;

  @Autowired
  public TeamMateController(ITeamMateService teamMateService) {
    this.teamMateService = teamMateService;
  }

  @GetMapping
  public List<TeamMate> getActiveTeamMates() {
    return teamMateService.getActiveTeamMates();
  }
}
