package com.artograd.api.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RestController
@EnableWebMvc
public class DemoController {
    @RequestMapping(path = "/hello", method = RequestMethod.GET)
    public String sayHello() {
        return "Hello from AWS Labmda '/hello' endpoint";
    }

    @RequestMapping(path = "/greet", method = RequestMethod.GET)
    public String greet(@RequestParam("userName") Optional<String> userName) {
        return String.format("Greetings %s!", userName);
    }

    @RequestMapping(path = "/whoami", method = RequestMethod.GET)
    public String printUserInfo(Principal principal)
    {
        if(principal == null)
            return "principal is empty";

        return principal.toString();
    }
}