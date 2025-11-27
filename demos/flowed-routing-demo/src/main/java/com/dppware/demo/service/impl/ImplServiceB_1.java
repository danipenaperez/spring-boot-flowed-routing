package com.dppware.demo.service.impl;

import com.dppware.demo.service.ServiceB;

import io.github.danipenaperez.lib.flowedrouting.annotation.RoutedComponent;

@RoutedComponent(isDefaultRouting = true)
public class ImplServiceB_1 implements ServiceB{

	@Override
	public String greeting(String language){
		return this.getClass().getName();
	}

}
