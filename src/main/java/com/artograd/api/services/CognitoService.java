package com.artograd.api.services;

import com.artograd.api.model.User;
import com.artograd.api.model.UserAttribute;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

import java.util.ArrayList;
import java.util.List;

@Service
public class CognitoService {
	
	@Value("${aws.cognito.userPoolId}")
    private String userPoolId;
    
	public User getUserBySub(String sub) {
        try (CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .build()) {

            ListUsersRequest listUsersRequest = ListUsersRequest.builder()
                    .userPoolId(userPoolId)
                    .filter(String.format("sub = \"%s\"", sub))
                    .build();
            
            ListUsersResponse listUsersResponse = cognitoClient.listUsers(listUsersRequest);
            List<UserAttribute> userAttrsResult = new ArrayList<UserAttribute>();
            for (UserType user : listUsersResponse.users()) {
                
            	List<AttributeType> list = user.attributes();
            	for (AttributeType attr : list) {
            		userAttrsResult.add(new UserAttribute(attr.name(), attr.value()));
				}
            	
            	return new User(userAttrsResult);
            }
        } catch (Exception e) {
            System.err.println("Error fetching user by sub: " + e.getMessage());
        }
        return null;
    }
}
