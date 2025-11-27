package com.dppware.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dppware.demo.service.GreetingService;

@RestController
public class GrettingController {

	@Autowired
	GreetingService greetingService;

	@GetMapping("/greeting")
	public String serviceA(@RequestParam("userName") String userName) {
		return greetingService.greeting(userName);
	}

}
