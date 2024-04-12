package com.artograd.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.CognitoUserPoolPreSignUpEvent;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersResponse;

public class PreSignUpLambdaHandler implements RequestHandler<CognitoUserPoolPreSignUpEvent, CognitoUserPoolPreSignUpEvent> {

    @Override
    public CognitoUserPoolPreSignUpEvent handleRequest(CognitoUserPoolPreSignUpEvent event, Context context) {
        try (CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .build()) {
            String userPoolId = event.getUserPoolId();
            ListUsersRequest usersRequest = ListUsersRequest.builder()
                    .userPoolId(userPoolId)
                    .filter(event.getUserName())
                    .build();
            ListUsersResponse response = cognitoClient.listUsers(usersRequest);
            if (!response.users().isEmpty()) {
                throw new RuntimeException("User with this email is already registered");
            }
            return event;
        }
    }
}
