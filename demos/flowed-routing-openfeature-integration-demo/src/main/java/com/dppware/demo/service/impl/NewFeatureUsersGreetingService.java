package com.dppware.demo.service.impl;

import com.dppware.demo.service.GreetingService;

import io.github.danipenaperez.lib.flowedrouting.annotation.RoutedComponent;
import io.github.danipenaperez.lib.flowedrouting.condition.FlowConditionType;
import io.github.danipenaperez.lib.flowedrouting.spel.annotation.FlowSpelCondition;

@RoutedComponent
public class NewFeatureUsersGreetingService  implements GreetingService{

	@FlowConditionType("SpEL") //Indicate use default evaluator provided in starter. Will use SpEL expressions
	@FlowSpelCondition(evaluationExpression = "@goFeatureFlagsClient.isFlagActiveForCurrentRequest('new-greeting-flag')") 
	@Override
	public String greeting(String userName){
		return "You are amazing "+userName +" because new-greeting-flag is active";
	}
}