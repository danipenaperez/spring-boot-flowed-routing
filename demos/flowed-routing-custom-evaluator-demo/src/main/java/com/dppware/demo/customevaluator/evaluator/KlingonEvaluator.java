package com.dppware.demo.customevaluator.evaluator;


import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

import io.github.danipenaperez.lib.flowedrouting.definition.RoutedMethodDefinition;
import io.github.danipenaperez.lib.flowedrouting.evaluator.Evaluator;
import io.github.danipenaperez.lib.flowedrouting.evaluator.annotation.EvaluatorType;
import lombok.extern.slf4j.Slf4j;
@Component
@EvaluatorType(value = "klingon")
@Slf4j
public class KlingonEvaluator implements Evaluator{

	
	@Override
	public boolean evaluate(RoutedMethodDefinition routedMethodDefinition, MethodInvocation invocation) {
		log.info("Using klingon evaluator");
		
		//Get annotation to evaluate
		KlingonCondition KlingonCondition = routedMethodDefinition.getMethod().getDeclaredAnnotation(KlingonCondition.class);
			
		if("Qapchu' functionality".equals(KlingonCondition.klingonExpression())){
				return true;
			}else {
				return false;
			}
		}
	}
