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

    public User getUserByUsername(String username) {
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
            return new User(userAttrsResult);
        } catch (Exception e) {
            System.err.println("Error fetching user by username: " + e.getMessage());
            return null;
        }
    }

    public UserTokenClaims getUserTokenClaims(HttpServletRequest request) {
        String region = userPoolId.substring(0, userPoolId.indexOf("_"));
        String cognitoIssuer = String.format("https://cognito-idp.%s.amazonaws.com/%s", region, userPoolId);

        JwkProvider provider = new JwkProviderBuilder(cognitoIssuer).build();

        RSAKeyProvider keyProvider = new RSAKeyProvider() {
            @Override
            public RSAPublicKey getPublicKeyById(String kid) {
                // Received 'kid' value might be null if it wasn't defined in the Token's header
                try {
                    return (RSAPublicKey) provider.get(kid).getPublicKey();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public RSAPrivateKey getPrivateKey() {
                return null; // Private key is not needed for token verification
            }

            @Override
            public String getPrivateKeyId() {
                return null; // Private key ID is not needed for token verification
            }
        };

        Algorithm algorithm = Algorithm.RSA256(keyProvider);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(cognitoIssuer)
                .build();

        String token = CommonUtils.parseToken(request);
        if (token != null) {
            DecodedJWT jwt = verifier.verify(CommonUtils.parseToken(request));
            String[] roles = jwt.getClaim("cognito:groups").asArray(String.class);

            UserTokenClaims tokenClaims = new UserTokenClaims();
            tokenClaims.setUsername(jwt.getClaim("cognito:username").asString());
            tokenClaims.setArtist(hasRole(roles, "Artists"));
            tokenClaims.setOfficer(hasRole(roles, "Officials"));

            return tokenClaims;
        } else {
            return new UserTokenClaims();
        }
    }

    private boolean hasRole(String[] roles, String r) {
        if (roles == null) {
            return false;
        }

        for (String role : roles) {
            if (role.equals(r)) {
                return true;
            }
        }
        return false;
    }
}
