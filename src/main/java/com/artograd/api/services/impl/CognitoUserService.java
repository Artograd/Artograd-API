package com.artograd.api.services.impl;

import com.artograd.api.model.User;
import com.artograd.api.model.UserAttribute;
import com.artograd.api.model.enums.UserAttributeKey;
import com.artograd.api.model.enums.UserRole;
import com.artograd.api.model.system.UserTokenClaims;
import com.artograd.api.services.IUserService;
import com.artograd.api.utils.CommonUtils;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import jakarta.servlet.http.HttpServletRequest;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminDeleteUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;

@Service
public class CognitoUserService implements IUserService {

  private static final Set<UserAttributeKey> ALWAYS_VISIBLE_ATTRIBUTES =
      EnumSet.of(
          UserAttributeKey.CUSTOM_FACEBOOK,
          UserAttributeKey.CUSTOM_INSTAGRAM,
          UserAttributeKey.CUSTOM_LINKEDIN,
          UserAttributeKey.CUSTOM_LOCATION,
          UserAttributeKey.CUSTOM_ORGANIZATION,
          UserAttributeKey.CUSTOM_JOBTITLE,
          UserAttributeKey.GIVEN_NAME,
          UserAttributeKey.FAMILY_NAME,
          UserAttributeKey.PICTURE,
          UserAttributeKey.COGNITO_USERNAME);
  private static final Set<UserAttributeKey> OWNER_ONLY_ATTRIBUTES =
      EnumSet.of(
          UserAttributeKey.CUSTOM_LANG_ISO2,
          UserAttributeKey.COGNITO_GROUPS,
          UserAttributeKey.EMAIL,
          UserAttributeKey.SHOW_EMAIL,
          UserAttributeKey.BANK_ACCOUNT,
          UserAttributeKey.BANK_BENEFICIARY,
          UserAttributeKey.BANK_BENEFICIARY_NAME,
          UserAttributeKey.BANK_IBAN,
          UserAttributeKey.BANK_SWIFT,
          UserAttributeKey.BANK_USE_DEFAULT,
          UserAttributeKey.PHONE_NUMBER);
  private static final Set<UserAttributeKey> OFFICIAL_VISIBLE_FOR_ARTISTS =
      EnumSet.of(UserAttributeKey.EMAIL, UserAttributeKey.PHONE_NUMBER);
  private static final Logger logger = LoggerFactory.getLogger(CognitoUserService.class);

  @Value("${aws.cognito.userPoolId}")
  private String userPoolId;

  @Override
  public boolean deleteUserByUsername(String userName) {
    try (CognitoIdentityProviderClient cognitoClient =
        CognitoIdentityProviderClient.builder().build()) {
      AdminDeleteUserRequest deleteRequest =
          AdminDeleteUserRequest.builder().userPoolId(userPoolId).username(userName).build();

      cognitoClient.adminDeleteUser(deleteRequest);
      return true;
    } catch (Exception e) {
      logger.error("Error deleting user by username: {}", e.getMessage(), e);
      return false;
    }
  }

  @Override
  public boolean updateUserAttributes(String userName, List<UserAttribute> attributes) {
    try (CognitoIdentityProviderClient cognitoClient =
        CognitoIdentityProviderClient.builder().build()) {
      List<AttributeType> attributeTypes = new ArrayList<>();
      for (UserAttribute userAttribute : attributes) {
        attributeTypes.add(
            AttributeType.builder()
                .name(userAttribute.getName())
                .value(userAttribute.getValue())
                .build());
      }

      AdminUpdateUserAttributesRequest updateRequest =
          AdminUpdateUserAttributesRequest.builder()
              .userPoolId(userPoolId)
              .username(userName)
              .userAttributes(attributeTypes)
              .build();

      cognitoClient.adminUpdateUserAttributes(updateRequest);
      return true;
    } catch (Exception e) {
      logger.error("Error updating user attributes by username: {}", e.getMessage(), e);
      return false;
    }
  }

  @Override
  public Optional<User> getUserByUsername(String username) {
    try (CognitoIdentityProviderClient cognitoClient =
        CognitoIdentityProviderClient.builder().build()) {
      AdminGetUserRequest getUserRequest =
          AdminGetUserRequest.builder().userPoolId(userPoolId).username(username).build();

      AdminGetUserResponse getUserResponse = cognitoClient.adminGetUser(getUserRequest);
      List<UserAttribute> userAttrsResult = new ArrayList<>();
      for (AttributeType attr : getUserResponse.userAttributes()) {
        userAttrsResult.add(new UserAttribute(attr.name(), attr.value()));
      }

      AdminListGroupsForUserRequest requestGetGroups =
          AdminListGroupsForUserRequest.builder().username(username).userPoolId(userPoolId).build();

      AdminListGroupsForUserResponse responseGroups =
          cognitoClient.adminListGroupsForUser(requestGetGroups);

      if (!responseGroups.groups().isEmpty()) {
        userAttrsResult.add(
            new UserAttribute("cognito:groups", responseGroups.groups().getFirst().groupName()));
      }

      return Optional.of(new User(userAttrsResult));
    } catch (Exception e) {
      logger.error("Error fetching user by username: {}", e.getMessage(), e);
      return Optional.empty();
    }
  }

  @Override
  public Optional<UserTokenClaims> getUserTokenClaims(HttpServletRequest request) {
    try {
      String cognitoIssuer = getCognitoIssuer(userPoolId);
      JwkProvider provider = new JwkProviderBuilder(cognitoIssuer).build();
      Algorithm algorithm = Algorithm.RSA256(new CognitoRSAKeyProvider(provider));

      JWTVerifier verifier = JWT.require(algorithm).withIssuer(cognitoIssuer).build();

      String token = CommonUtils.parseToken(request);
      if (token != null) {
        DecodedJWT jwt = verifier.verify(token);
        // uncomment it for local usage and debugging because time in AWS may differ from local time
        // and
        // token may be invalid because earliest usage time is before issuing
        // DecodedJWT jwt = JWT.decode(token);
        return Optional.of(extractClaims(jwt));
      }
    } catch (Exception e) {
      logger.error("Error fetching user token claims: {}", e.getMessage(), e);
    }
    return Optional.empty();
  }

  @Override
  public List<UserAttribute> filterAttributes(
      List<UserAttribute> attributes,
      UserRole requesterRole,
      boolean isProfileOwner,
      UserRole profileRole) {
    return attributes.stream()
        .filter(attr -> shouldIncludeAttribute(attr, requesterRole, isProfileOwner, profileRole))
        .toList();
  }

  private boolean shouldIncludeAttribute(
      UserAttribute attribute,
      UserRole requesterRole,
      boolean isProfileOwner,
      UserRole profileRole) {
    UserAttributeKey attributeKey;
    try {
      attributeKey = UserAttributeKey.fromString(attribute.getName());
    } catch (IllegalArgumentException e) {
      return false;
    }
    if (isAttributeVisibleToEveryone(attributeKey)) {
      return true;
    }
    if (isAttributeVisibleOnlyToProfileOwner(attributeKey, isProfileOwner)) {
      return true;
    }
    if (isAttributeInArtistProfileAndOfficersHaveAccess(attributeKey, requesterRole, profileRole)) {
      return true;
    }
    return isAttributeInOfficerProfileAndOfficersHaveAccess(
        attributeKey, requesterRole, profileRole);
  }

  private boolean isAttributeVisibleToEveryone(UserAttributeKey attributeName) {
    return ALWAYS_VISIBLE_ATTRIBUTES.contains(attributeName);
  }

  private boolean isAttributeVisibleOnlyToProfileOwner(
      UserAttributeKey attributeName, boolean isProfileOwner) {
    return isProfileOwner && OWNER_ONLY_ATTRIBUTES.contains(attributeName);
  }

  private boolean isAttributeInArtistProfileAndOfficersHaveAccess(
      UserAttributeKey attributeName, UserRole requesterRole, UserRole profileRole) {
    return profileRole == UserRole.ARTIST
        && requesterRole == UserRole.OFFICIAL
        && OFFICIAL_VISIBLE_FOR_ARTISTS.contains(attributeName);
  }

  private boolean isAttributeInOfficerProfileAndOfficersHaveAccess(
      UserAttributeKey attributeName, UserRole requesterRole, UserRole profileRole) {
    return profileRole == UserRole.OFFICIAL
        && requesterRole == UserRole.OFFICIAL
        && OFFICIAL_VISIBLE_FOR_ARTISTS.contains(attributeName);
  }

  private String getCognitoIssuer(String userPoolId) {
    String region = userPoolId.substring(0, userPoolId.indexOf("_"));
    return String.format("https://cognito-idp.%s.amazonaws.com/%s", region, userPoolId);
  }

  private UserTokenClaims extractClaims(DecodedJWT jwt) {
    UserTokenClaims tokenClaims = new UserTokenClaims();
    tokenClaims.setUsername(jwt.getClaim("cognito:username").asString());
    String[] roles = jwt.getClaim("cognito:groups").asArray(String.class);
    tokenClaims.setUserRole(extractUserRole(roles));
    tokenClaims.setOfficer(tokenClaims.getUserRole().equals(UserRole.OFFICIAL));
    tokenClaims.setArtist(tokenClaims.getUserRole().equals(UserRole.ARTIST));
    return tokenClaims;
  }

  private UserRole extractUserRole(String[] roles) {
    if (roles == null) {
      return UserRole.ANONYMOUS_OR_CITIZEN;
    }
    if (roles.length != 1) {
      throw new IllegalArgumentException(
          "Token has " + roles.length + " groups when only 1 is allowed");
    }
    return UserRole.fromString(roles[0]);
  }

  private static class CognitoRSAKeyProvider implements RSAKeyProvider {

    private final JwkProvider jwkProvider;

    public CognitoRSAKeyProvider(JwkProvider jwkProvider) {
      this.jwkProvider = jwkProvider;
    }

    @Override
    public RSAPublicKey getPublicKeyById(String kid) {
      try {
        return (RSAPublicKey) jwkProvider.get(kid).getPublicKey();
      } catch (Exception e) {
        logger.error("Error fetching public key: {}", e.getMessage(), e);
        return null;
      }
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
      return null;
    }

    @Override
    public String getPrivateKeyId() {
      return null;
    }
  }
}
