package io.github.danipenaperez.lib.flowedrouting.evaluator;

import org.aopalliance.intercept.MethodInvocation;

import io.github.danipenaperez.lib.flowedrouting.definition.RoutedMethodDefinition;

/**
 * Common interface to be implemented for all Evaluators
 */
public interface Evaluator {

	public boolean evaluate(RoutedMethodDefinition routedMethodDefinition, MethodInvocation invocation);
}
