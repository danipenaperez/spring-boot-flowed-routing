package com.dppware.demo.customevaluator.service;

import io.github.danipenaperez.lib.flowedrouting.annotation.RoutedInterface;

@RoutedInterface
public interface GreetingService {

	public String greeting(String userName);
}
