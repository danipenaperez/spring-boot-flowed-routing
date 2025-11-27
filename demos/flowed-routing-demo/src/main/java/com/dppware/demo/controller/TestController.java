package com.dppware.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dppware.demo.service.GreetingService;
import com.dppware.demo.service.ServiceB;

@RestController
public class TestController {

	@Autowired
	GreetingService greetingService;
	
	@Autowired
	ServiceB serviceB;
	
	@GetMapping("/greeting")
    public String serviceA(@RequestParam("language") String language) {
        return greetingService.greeting(language);
    }
	
	@GetMapping("/serviceB")
    public String serviceB() {
        return serviceB.greeting("as");
    }
}
