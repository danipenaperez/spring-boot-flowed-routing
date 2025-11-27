package com.dppware.demo.customevaluator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dppware.demo.customevaluator.service.GreetingService;

@RestController
public class TestController {

	@Autowired
	GreetingService greetingService;
	
	@GetMapping("/greeting")
    public String serviceA(@RequestParam("language") String language) {
        return greetingService.greeting(language);
    }

}
