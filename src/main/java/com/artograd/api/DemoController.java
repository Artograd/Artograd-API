package com.artograd.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RestController
@EnableWebMvc
public class DemoController {
    @RequestMapping(path = "/hello", method = RequestMethod.GET)
    public String sayHello() {
        return "Hello from AWS Labmda /hello endpoint";
    }

    @RequestMapping(path = "/greet", method = RequestMethod.GET)
    public String greet(String userName) {
        return String.format("Greetings %s!", userName);
    }
}