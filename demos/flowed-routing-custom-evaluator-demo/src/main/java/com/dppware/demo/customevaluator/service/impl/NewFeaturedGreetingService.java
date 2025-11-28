package com.dppware.demo.customevaluator.service.impl;

import com.dppware.demo.customevaluator.evaluator.KlingonCondition;
import com.dppware.demo.customevaluator.service.GreetingService;

import io.github.danipenaperez.lib.flowedrouting.annotation.RoutedComponent;
import io.github.danipenaperez.lib.flowedrouting.condition.FlowConditionType;

@RoutedComponent
public class NewFeaturedGreetingService  implements GreetingService{

	@FlowConditionType("klingon") //Indicate use klingon evaluator
	@KlingonCondition(klingonExpression = "Qapchu' functionality") 
	@Override
	public String greeting(String username){
		return "New Greeting Service is enabled for you "+username+ " [klingon accepted request]";
	}
}
