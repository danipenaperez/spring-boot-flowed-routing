package com.dppware.demo.service;

import io.github.danipenaperez.lib.flowedrouting.annotation.RoutedInterface;

@RoutedInterface
public interface GreetingService {

	public String greeting(String userName);
}
