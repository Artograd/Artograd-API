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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CognitoUserService implements IUserService {
	
	private static final Logger logger = LoggerFactory.getLogger(CognitoUserService.class);

    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;

    @Override
    public boolean deleteUserByUsername(String userName) {
        try (CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder().build()) {
            AdminDeleteUserRequest deleteRequest = AdminDeleteUserRequest.builder()
                .userPoolId(userPoolId)
                .username(userName)
                .build();

            cognitoClient.adminDeleteUser(deleteRequest);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting user by username: ", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateUserAttributes(String userName, List<UserAttribute> attributes) {
        try (CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder().build()) {
            List<AttributeType> attributeTypes = new ArrayList<>();
            for (UserAttribute userAttribute : attributes) {
                attributeTypes.add(AttributeType.builder()
                    .name(userAttribute.getName())
                    .value(userAttribute.getValue())
                    .build());
            }

            AdminUpdateUserAttributesRequest updateRequest = AdminUpdateUserAttributesRequest.builder()
                .userPoolId(userPoolId)
                .username(userName)
                .userAttributes(attributeTypes)
                .build();

            cognitoClient.adminUpdateUserAttributes(updateRequest);
            return true;
        } catch (Exception e) {
            logger.error("Error updating user attributes by username: ", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        try (CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder().build()) {
            AdminGetUserRequest getUserRequest = AdminGetUserRequest.builder()
                .userPoolId(userPoolId)
                .username(username)
                .build();

            AdminGetUserResponse getUserResponse = cognitoClient.adminGetUser(getUserRequest);
            List<UserAttribute> userAttrsResult = new ArrayList<>();
            for (AttributeType attr : getUserResponse.userAttributes()) {
                userAttrsResult.add(new UserAttribute(attr.name(), attr.value()));
            }
            
            AdminListGroupsForUserRequest requestGetGroups = AdminListGroupsForUserRequest.builder()
                .username(username)
                .userPoolId(userPoolId)
                .build();
            
            AdminListGroupsForUserResponse responseGroups = cognitoClient.adminListGroupsForUser(requestGetGroups);
            
            if (responseGroups.groups().size() > 0)  {
            	userAttrsResult.add(new UserAttribute("cognito:groups", responseGroups.groups().get(0).groupName()));
            }
            
            return Optional.ofNullable(new User(userAttrsResult));
        } catch (Exception e) {
            logger.error("Error fetching user by username: ", e.getMessage(), e);
            return Optional.ofNullable(null);
        }
    }
    
    @Override
    public Optional<UserTokenClaims> getUserTokenClaims(HttpServletRequest request) {
        try {
            String cognitoIssuer = getCognitoIssuer(userPoolId);
            JwkProvider provider = new JwkProviderBuilder(cognitoIssuer).build();
            Algorithm algorithm = Algorithm.RSA256(new CognitoRSAKeyProvider(provider));

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(cognitoIssuer)
                    .build();

            String token = CommonUtils.parseToken(request);
            if (token != null) {
                DecodedJWT jwt = verifier.verify(token);
                return Optional.of(extractClaims(jwt));
            }
        } catch (Exception e) {
            logger.error("Error fetching user token claims ", e.getMessage(), e);
        }
        return Optional.empty();
    }
    
    @Override
    public UserRole determineRequesterRole(UserTokenClaims claims) {
    	if ( claims == null ) {
    		return UserRole.ANONYMOUS_OR_CITIZEN;
    	}
    	
        if (claims.isArtist()) return UserRole.ARTIST;
        if (claims.isOfficer()) return UserRole.OFFICIAL;
        return UserRole.ANONYMOUS_OR_CITIZEN;
    }

    @Override
    public List<UserAttribute> filterAttributes(List<UserAttribute> attributes, UserRole requesterRole, boolean isProfileOwner, UserRole profileRole) {
        return attributes.stream()
                .filter(attr -> {
                    try {
                        return shouldIncludeAttribute(UserAttributeKey.valueOf(attr.getName().toUpperCase().replace(":", "_").replace("-", "_")), requesterRole, isProfileOwner, profileRole);
                    } catch (IllegalArgumentException e) {
                        // If no enum constant is found, skip this attribute
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    private boolean shouldIncludeAttribute(UserAttributeKey attributeName, UserRole requesterRole, boolean isProfileOwner, UserRole profileRole) {
        Set<UserAttributeKey> alwaysVisibleAttributes = EnumSet.of(
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
        
        Set<UserAttributeKey> ownerOnlyAttributes = EnumSet.of(
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
        
        Set<UserAttributeKey> officialVisibleForArtists = EnumSet.of(
        		UserAttributeKey.EMAIL,            
        		UserAttributeKey.PHONE_NUMBER);

        // Attributes visible to everyone
        if (alwaysVisibleAttributes.contains(attributeName)) {
            return true;
        }

        // Attributes visible only to the profile owner
        if (isProfileOwner && ownerOnlyAttributes.contains(attributeName)) {
            return true;
        }

        // Email and phone_number are visible to officers when viewing an artist's profile6
        if (profileRole == UserRole.ARTIST && requesterRole == UserRole.OFFICIAL && officialVisibleForArtists.contains(attributeName)) {
            return true;
        }

        // Email and phone_number are visible to officers when viewing another officer's profile
        if (profileRole == UserRole.OFFICIAL && requesterRole == UserRole.OFFICIAL && officialVisibleForArtists.contains(attributeName)) {
            return true;
        }

        return false;
    }

    private String getCognitoIssuer(String userPoolId) {
        String region = userPoolId.substring(0, userPoolId.indexOf("_"));
        return String.format("https://cognito-idp.%s.amazonaws.com/%s", region, userPoolId);
    }

    private UserTokenClaims extractClaims(DecodedJWT jwt) {
        UserTokenClaims tokenClaims = new UserTokenClaims();
        tokenClaims.setUsername(jwt.getClaim("cognito:username").asString());
        String[] roles = jwt.getClaim("cognito:groups").asArray(String.class);
        tokenClaims.setArtist(hasRole(roles, "Artists"));
        tokenClaims.setOfficer(hasRole(roles, "Officials"));
        return tokenClaims;
    }

    private boolean hasRole(String[] roles, String role) {
        if (roles != null) {
            for (String r : roles) {
                if (r.equals(role)) {
                    return true;
                }
            }
        }
        return false;
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
            	logger.error("Error fetching public key:", e.getMessage(), e);
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
