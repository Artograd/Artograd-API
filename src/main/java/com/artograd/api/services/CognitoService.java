package com.artograd.api.services;

import com.artograd.api.model.User;
import com.artograd.api.model.UserAttribute;
import com.artograd.api.model.system.UserTokenClaims;
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

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CognitoService {

    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;

    public boolean deleteUserByUsername(String userName) {
        try (CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder().build()) {
            AdminDeleteUserRequest deleteRequest = AdminDeleteUserRequest.builder()
                .userPoolId(userPoolId)
                .username(userName)
                .build();

            cognitoClient.adminDeleteUser(deleteRequest);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting user by sub: " + e.getMessage());
            return false;
        }
    }

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
            System.err.println("Error updating user attributes by username: " + e.getMessage());
            return false;
        }
    }

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
            return Optional.ofNullable(new User(userAttrsResult));
        } catch (Exception e) {
            System.err.println("Error fetching user by username: " + e.getMessage());
            return Optional.ofNullable(null);
        }
    }
    
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
            e.printStackTrace();
        }
        return Optional.empty();
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
                e.printStackTrace(); // Consider proper logging
                return null;
            }
        }

        @Override
        public RSAPrivateKey getPrivateKey() {
            return null; // Not needed for token verification
        }

        @Override
        public String getPrivateKeyId() {
            return null; // Not needed for token verification
        }
    }
}
