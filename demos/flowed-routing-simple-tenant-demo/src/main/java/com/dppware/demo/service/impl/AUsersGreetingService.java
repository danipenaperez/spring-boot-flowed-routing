package com.dppware.demo.service.impl;

import com.dppware.demo.service.GreetingService;

import io.github.danipenaperez.lib.flowedrouting.annotation.RoutedComponent;
import io.github.danipenaperez.lib.flowedrouting.condition.FlowConditionType;
import io.github.danipenaperez.lib.flowedrouting.spel.annotation.FlowSpelCondition;

@RoutedComponent
public class AUsersGreetingService  implements GreetingService{

	@FlowConditionType("SpEL") //Indicate use default evaluator provided in starter. Will use SpEL expressions
	@FlowSpelCondition(evaluationExpression = "#userName.startsWith('A') && @executionContext.getTenantName() == 'tenant_1'") 
	@Override
	public String greeting(String userName){
		return "You are amazing "+userName +" because you owns to tenant_1";
	}
}