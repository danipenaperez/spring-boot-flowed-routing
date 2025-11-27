package io.github.danipenaperez.lib.flowedrouting.spel;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import io.github.danipenaperez.lib.flowedrouting.definition.RoutedMethodDefinition;
import io.github.danipenaperez.lib.flowedrouting.evaluator.Evaluator;
import io.github.danipenaperez.lib.flowedrouting.evaluator.annotation.EvaluatorType;
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
	public boolean evaluate(RoutedMethodDefinition routedMethodDefinition, MethodInvocation invocation) {
		FlowSpelCondition spelCondition = routedMethodDefinition.getMethod().getAnnotation(FlowSpelCondition.class);
		StandardEvaluationContext evaluationContext = assembleEvaluationContext(invocation);
		return new SpelExpressionParser().parseExpression(spelCondition.evaluationExpression()).getValue(evaluationContext, Boolean.class);
	}
}