package com.dppware.demo.service;

import io.github.danipenaperez.lib.flowedrouting.annotation.RoutedInterface;

@RoutedInterface
public interface ServiceB {
	public String greeting(String language);
}
