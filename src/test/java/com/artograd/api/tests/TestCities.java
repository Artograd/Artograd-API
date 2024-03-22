package com.artograd.api.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.artograd.api.taf.TestService;

import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class TestCities {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private TestService testService;

    /**
     * Test that cities are returned with authorization token
     * @throws Exception
     */
    @Test
    public void testCitiesEndpointAsOfficial() throws Exception {
        mockMvc.perform(get("/cities")
            .header("Authorization", "Bearer " + testService.getTestUsers().getOfficalToken())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }
    
    /**
     * Test that cities are returned with authorization token
     * @throws Exception
     */
    @Test
    public void testCitiesEndpointAsCitizen() throws Exception {
        mockMvc.perform(get("/cities")
            .header("Authorization", "Bearer " + testService.getTestUsers().getCitizenToken())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }
    
    /**
     * Test that cities are returned without authorization token
     * @throws Exception
     */
    @Test
    public void testCitiesEndpointAsAnonymous() throws Exception {
        mockMvc.perform(get("/cities")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    /**
     * Test that cities are returned without authorization token
     * @throws Exception
     */
    @Test
    public void testCitiesEndpointAsBrokenToken() throws Exception {
        mockMvc.perform(get("/cities")
    		.header("Authorization", "Bearer null")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }
}
