package com.dppware.demo.service.impl;

import com.dppware.demo.service.GreetingService;

import io.github.danipenaperez.lib.flowedrouting.annotation.RoutedComponent;
import io.github.danipenaperez.lib.flowedrouting.condition.FlowConditionType;
import io.github.danipenaperez.lib.flowedrouting.spel.annotation.FlowSpelCondition;

@RoutedComponent
public class SpanishGreetingService  implements GreetingService{

	@FlowConditionType("SpEL")
//	@FlowSpelCondition(evaluationExpression = "#language == 'es'")
	@FlowSpelCondition(evaluationExpression = "@executionContext.getTenantName() == 'spain' && #language == 'es'")
	@Override
	public String greeting(String language){
		return this.getClass().getName();
	}

}
