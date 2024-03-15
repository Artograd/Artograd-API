package com.artograd.api.controllers;

import com.artograd.api.model.User;
import com.artograd.api.services.CognitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private CognitoService cognitoService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserAttributesById(@PathVariable String id) {
        return new ResponseEntity<>(cognitoService.getUserBySub(id), HttpStatus.OK);
    }

}
