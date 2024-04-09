package com.artograd.api.tests;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.artograd.api.taf.ITestService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestTenderAndProposal {

  @Autowired private MockMvc mockMvc;

  @Autowired private ITestService testService;

  private static String tenderId;
  private static String proposalId;

  @Test
  @Order(1)
  void nonOfficialCreatesTender_Forbidden() throws Exception {
    mockMvc
        .perform(
            post("/tenders")
                .header("Authorization", "Bearer " + testService.getTestUsers().getCitizenToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(testService.getDefaultTenderJson()))
        .andExpect(status().isForbidden());
  }

  @Test
  @Order(2)
  void officialCreatesTender_Success() throws Exception {
    mockMvc
        .perform(
            post("/tenders")
                .header("Authorization", "Bearer " + testService.getTestUsers().getOfficalToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(testService.getDefaultTenderJson()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.ownerId", containsString("officer")))
        .andDo(
            result -> tenderId = JsonPath.read(result.getResponse().getContentAsString(), "$.id"));
  }

  @Test
  @Order(3)
  void searchTenderByTitle_Success() throws Exception {
    String searchTitle = "New Art Installation";

    mockMvc
        .perform(
            get("/tenders").contentType(MediaType.APPLICATION_JSON).param("title", searchTitle))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(greaterThan(0))))
        .andExpect(jsonPath("$[0].title", is(searchTitle)));
  }

  @Test
  @Order(4)
  void officerUpdatesTender_Success() throws Exception {
    String tenderJson = testService.getDefaultTenderJson();
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(tenderJson);

    ((ObjectNode) root).put("id", tenderId);
    ((ObjectNode) root).put("title", "New Title");
    String updatedJson = mapper.writeValueAsString(root);

    mockMvc
        .perform(
            put("/tenders/" + tenderId)
                .header("Authorization", "Bearer " + testService.getTestUsers().getOfficalToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("New Title"));
  }

  @Test
  @Order(5)
  void officerCreatesProposalForTender_Forbidden() throws Exception {
    mockMvc
        .perform(
            post("/tenders/" + tenderId + "/proposals")
                .header("Authorization", "Bearer " + testService.getTestUsers().getOfficalToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(testService.getDefaultProposalJson()))
        .andExpect(status().isForbidden());
  }

  @Test
  @Order(6)
  void creatorCreatesProposalForTender_Success() throws Exception {
    mockMvc
        .perform(
            post("/tenders/" + tenderId + "/proposals")
                .header("Authorization", "Bearer " + testService.getTestUsers().getCreatorToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(testService.getDefaultProposalJson()))
        .andExpect(status().isCreated())
        .andDo(
            result ->
                proposalId = JsonPath.read(result.getResponse().getContentAsString(), "$.id"));
  }

  @Test
  @Order(7)
  void creatorUpdatesProposal_Success() throws Exception {
    String proposalJson = testService.getDefaultProposalJson();
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(proposalJson);

    ((ObjectNode) root).put("id", proposalId);
    ((ObjectNode) root).put("title", "New Title");
    String updatedJson = mapper.writeValueAsString(root);

    mockMvc
        .perform(
            put("/tenders/" + tenderId + "/proposals/" + proposalId)
                .header("Authorization", "Bearer " + testService.getTestUsers().getCreatorToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("New Title"));
  }

  @Test
  @Order(8)
  void officialDeletesProposal_Forbidden() throws Exception {
    mockMvc
        .perform(
            delete("/tenders/" + tenderId + "/proposals/" + proposalId)
                .header("Authorization", "Bearer " + testService.getTestUsers().getOfficalToken()))
        .andExpect(status().isForbidden());
  }

  @Test
  @Order(9)
  void creatorDeletesProposal_Success() throws Exception {
    mockMvc
        .perform(
            delete("/tenders/" + tenderId + "/proposals/" + proposalId)
                .header("Authorization", "Bearer " + testService.getTestUsers().getCreatorToken()))
        .andExpect(status().isNoContent());
  }

  @Test
  @Order(10)
  void creatorDeletesProposalAgain_Forbidden() throws Exception {
    mockMvc
        .perform(
            delete("/tenders/" + tenderId + "/proposals/" + proposalId)
                .header("Authorization", "Bearer " + testService.getTestUsers().getCreatorToken()))
        .andExpect(status().isForbidden());
  }

  @Test
  @Order(11)
  void creatorDeletesTender_Forbidden() throws Exception {
    mockMvc
        .perform(
            delete("/tenders/" + tenderId)
                .header("Authorization", "Bearer " + testService.getTestUsers().getCreatorToken()))
        .andExpect(status().isForbidden());
  }

  @Test
  @Order(12)
  void officerDeletesTender_Success() throws Exception {
    mockMvc
        .perform(
            delete("/tenders/" + tenderId)
                .header("Authorization", "Bearer " + testService.getTestUsers().getOfficalToken()))
        .andExpect(status().isNoContent());
  }
}
