package io.github.danipenaperez.lib.flowedrouting.interceptor;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultFlowRoutingInterceptor extends FlowRoutingInterceptor implements MethodInterceptor {
	

	public DefaultFlowRoutingInterceptor(Class wrappedInterfaceClass) {
		super(wrappedInterfaceClass);
	}
	
	
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		var parameterTypes = invocation.getMethod().getParameterTypes();
		Method targetMethod = wrappedInterfaceClass.getMethod(method.getName(), parameterTypes);
		targetMethod.setAccessible(true);
		Object beanDelegate = findDelegate(targetMethod, invocation );
		return targetMethod.invoke(beanDelegate, invocation.getArguments());
	}
	
	
}
