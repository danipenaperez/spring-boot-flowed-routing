package io.github.danipenaperez.lib.flowedrouting.definition;

import java.lang.reflect.Method;

import io.github.danipenaperez.lib.flowedrouting.condition.FlowConditionType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoutedMethodDefinition {
	Method method;
	FlowConditionType flowCondition;
	Object bean;
	boolean isDefault;
}
