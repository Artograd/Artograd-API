package com.artograd.api.taf;

public interface ITestService {

  TestUsers getTestUsers();

  String getIdToken(String username, String password);

  String getDefaultTenderJson();

  String getDefaultProposalJson();
}
