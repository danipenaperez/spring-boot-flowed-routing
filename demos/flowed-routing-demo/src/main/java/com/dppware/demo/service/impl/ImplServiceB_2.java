package com.dppware.demo.service.impl;

import com.dppware.demo.service.ServiceB;

import io.github.danipenaperez.lib.flowedrouting.annotation.RoutedComponent;
import io.github.danipenaperez.lib.flowedrouting.condition.FlowConditionType;
import io.github.danipenaperez.lib.flowedrouting.spel.annotation.FlowSpelCondition;

@RoutedComponent
public class ImplServiceB_2 implements ServiceB{

	@FlowConditionType("SpEL")
	@FlowSpelCondition(evaluationExpression = "1 == 1")
	@Override
	public String greeting(String language){
		return this.getClass().getName();
	}

}
