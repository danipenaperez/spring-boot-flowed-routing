package com.dppware.demo.customevaluator.service.impl;

import com.dppware.demo.customevaluator.service.GreetingService;

import io.github.danipenaperez.lib.flowedrouting.annotation.RoutedComponent;

@RoutedComponent(isDefaultRouting = true)
public class DefaultGreetingService  implements GreetingService{

	@Override
	public String greeting(String userName){
		return "Default Greeting for "+userName;
	}

}
