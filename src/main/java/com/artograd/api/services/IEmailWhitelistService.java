package com.artograd.api.services;

public interface IEmailWhitelistService {
  boolean isEmailWhitelisted(String email);
}
