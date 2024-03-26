package com.artograd.api.taf;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;

@Service
public class TestService implements ITestService {

  @Value("${aws.cognito.clientId}")
  private String clientId;

  @Value("${artograd.user-official}")
  private String officialCredentials;

  @Value("${artograd.user-creator}")
  private String creatorCredentials;

  @Value("${artograd.user-citizen}")
  private String citizenCredentials;

  private final TestUsers testUsers = new TestUsers();

  @PostConstruct
  public void init() {
    testUsers.setOfficalToken(
        getIdToken(officialCredentials.split(":")[0], officialCredentials.split(":")[1]));
    testUsers.setCreatorToken(
        getIdToken(creatorCredentials.split(":")[0], creatorCredentials.split(":")[1]));
    testUsers.setCitizenToken(
        getIdToken(citizenCredentials.split(":")[0], citizenCredentials.split(":")[1]));
  }

  public TestUsers getTestUsers() {
    return testUsers;
  }

  public String getIdToken(String username, String password) {
    Map<String, String> authParameters = new HashMap<>();
    authParameters.put("USERNAME", username);
    authParameters.put("PASSWORD", password);

    InitiateAuthRequest authRequest =
        InitiateAuthRequest.builder()
            .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
            .clientId(clientId)
            .authParameters(authParameters)
            .build();

    try {
      CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder().build();
      InitiateAuthResponse authResponse = cognitoClient.initiateAuth(authRequest);
      return authResponse.authenticationResult().idToken();
    } catch (Exception e) {
      throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
    }
  }

  public String getDefaultTenderJson() {
    return getDefaultObjectJson("src/test/resources/json/Tender.json");
  }

  public String getDefaultProposalJson() {
    return getDefaultObjectJson("src/test/resources/json/Proposal.json");
  }

  private String getDefaultObjectJson(String path) {
    final Path tenderJsonPath = Paths.get(path);
    try {
      return Files.readString(tenderJsonPath, StandardCharsets.UTF_8);
    } catch (IOException e) {
      return "{}";
    }
  }
}
