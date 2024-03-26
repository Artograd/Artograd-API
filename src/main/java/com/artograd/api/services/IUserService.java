package com.artograd.api.services;

import com.artograd.api.model.User;
import com.artograd.api.model.UserAttribute;
import com.artograd.api.model.system.UserTokenClaims;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface IUserService {
  boolean deleteUserByUsername(String userName);

  boolean updateUserAttributes(String userName, List<UserAttribute> attributes);

  Optional<User> getUserByUsername(String username);

  Optional<UserTokenClaims> getUserTokenClaims(HttpServletRequest request);
}
