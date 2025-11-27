package com.dppware.demo.service.impl;

import com.dppware.demo.service.GreetingService;

import io.github.danipenaperez.lib.flowedrouting.annotation.RoutedComponent;

@RoutedComponent(isDefaultRouting = true)
public class DefaultGreetingService  implements GreetingService{

	@Override
	public String greeting(String language){
		return "ejecutando Default para language "+language;
	}

}
