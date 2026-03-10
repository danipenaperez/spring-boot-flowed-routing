package io.github.danipenaperez.lib.flowedrouting.spel;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Optional;

import io.github.danipenaperez.lib.flowedrouting.definition.RoutedMethodDefinition;
import io.github.danipenaperez.lib.flowedrouting.evaluator.Evaluator;
import io.github.danipenaperez.lib.flowedrouting.evaluator.annotation.EvaluatorType;
import io.github.danipenaperez.lib.flowedrouting.exception.FlowedRoutingConfigurationException;
import io.github.danipenaperez.lib.flowedrouting.exception.FlowedRoutingRuntimeException;
import io.github.danipenaperez.lib.flowedrouting.spel.annotation.FlowSpelCondition;

@EvaluatorType("SpEL")
public class SpELEvaluator implements Evaluator{

	protected ApplicationContext applicationContext;
	protected BeanFactoryResolver beanFactoryResolver;
	
	public SpELEvaluator(ApplicationContext applicationContext) {
		this.applicationContext=applicationContext;
		this.beanFactoryResolver= new BeanFactoryResolver(this.applicationContext);
	}

	protected StandardEvaluationContext assembleEvaluationContext(MethodInvocation invocation) {
		
		StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
		evaluationContext.setBeanResolver(beanFactoryResolver);//Set Spring context accesor
		for(int i=0 ;i < invocation.getMethod().getParameters().length;i++) {
			evaluationContext.setVariable(invocation.getMethod().getParameters()[i].getName(), invocation.getArguments()[i]);
		}
		
		return evaluationContext;
		
	}

	@Override
	public boolean evaluate(RoutedMethodDefinition routedMethodDefinition, MethodInvocation invocation) throws FlowedRoutingRuntimeException {
		FlowSpelCondition spelCondition = Optional.ofNullable(routedMethodDefinition.getMethod().getAnnotation(FlowSpelCondition.class))
				.orElseThrow(() -> new FlowedRoutingRuntimeException(
						String.format("Method [%s] uses SpEL evaluator but is missing @FlowSpelCondition annotation",
								routedMethodDefinition.getMethod().getName())));
		StandardEvaluationContext evaluationContext = assembleEvaluationContext(invocation);
		Expression expression = Optional.ofNullable(new SpelExpressionParser().parseExpression(spelCondition.evaluationExpression()))
				.orElseThrow(() -> new FlowedRoutingRuntimeException(
						String.format("Could not parse SpEL expression [%s] on method [%s]",
								spelCondition.evaluationExpression(), routedMethodDefinition.getMethod().getName())));
		Boolean result = expression.getValue(evaluationContext, Boolean.class);
		return Boolean.TRUE.equals(result);
	}
}