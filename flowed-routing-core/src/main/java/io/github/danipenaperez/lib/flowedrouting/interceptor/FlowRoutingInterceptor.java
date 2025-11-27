package io.github.danipenaperez.lib.flowedrouting.interceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aopalliance.intercept.MethodInvocation;

import io.github.danipenaperez.lib.flowedrouting.annotation.RoutedComponent;
import io.github.danipenaperez.lib.flowedrouting.condition.FlowConditionType;
import io.github.danipenaperez.lib.flowedrouting.definition.RoutedMethodDefinition;
import io.github.danipenaperez.lib.flowedrouting.evaluator.Evaluator;
import io.github.danipenaperez.lib.flowedrouting.evaluator.annotation.EvaluatorType;
import io.github.danipenaperez.lib.flowedrouting.exception.FlowedRoutingConfigurationException;
import io.github.danipenaperez.lib.flowedrouting.utils.TypeUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Main interface for all Interceptor
 */
@Getter
@Slf4j
public abstract class FlowRoutingInterceptor {

	protected Class wrappedInterfaceClass ;
	protected List<Object> delegatedImplementations = new ArrayList<>();
	protected Map<String,Evaluator> evaluatorImplementations = new HashMap<>();
	
	Map<String, List<RoutedMethodDefinition>> delegatedMethodsTree = new HashMap<>();
	
	public FlowRoutingInterceptor(Class wrappedInterfaceClass) {
		this.wrappedInterfaceClass=wrappedInterfaceClass;
	}
	
	public void addDelegates(Collection<Object> delegates) throws FlowedRoutingConfigurationException{
		for(Object delegate: delegates) {
			if(!TypeUtils.isProxy(delegate)) {
				delegatedImplementations.add(delegate);
			}
		}
		refresh();
	}
	
	public void registerEvaluator(Evaluator evaluator) {
		evaluatorImplementations.put(evaluator.getClass().getAnnotation(EvaluatorType.class).value(), evaluator);
	}
	
	
	protected void refresh() throws FlowedRoutingConfigurationException{
		
		for(Object delegate: delegatedImplementations){
			var targetMethodsDefinition = wrappedInterfaceClass.getDeclaredMethods();	
			for(Method targetInterfaceMethodDefinition: targetMethodsDefinition) {
				try {
					//InspectMethod delegate.getClass()
					String methodKey = targetInterfaceMethodDefinition.toString();
					List<RoutedMethodDefinition> candidateExecutions = delegatedMethodsTree.get(methodKey);
					if(candidateExecutions == null) {
						candidateExecutions = new ArrayList<>();
						delegatedMethodsTree.put(methodKey, candidateExecutions);
					}
					
					//Method
					Method beanMethodDefinition = delegate.getClass().getMethod(targetInterfaceMethodDefinition.getName(), targetInterfaceMethodDefinition.getParameterTypes());
					boolean isDefault = delegate.getClass().getAnnotation(RoutedComponent.class).isDefaultRouting();
					Object bean = delegate;
					FlowConditionType fc = null; //Default RoutedComponent all method does not need flow condition
					if(! isDefault) { //Must check internal method annnotations
						//Flow Condition
						fc = beanMethodDefinition.getAnnotation(FlowConditionType.class);
						if(evaluatorImplementations.get(fc.value()) == null) {
							throw new FlowedRoutingConfigurationException(String.format("[%s] declares FlowConditionType = [%s] , but not found any Evaluator of this type at Spring Context", delegate.getClass().getName(), fc.value()));
						}	
					}
					
					//Validated and store for usages
					candidateExecutions.add(new RoutedMethodDefinition(beanMethodDefinition, fc,bean,isDefault));
					
				}catch (Exception e) {
					log.error("Error while assembling delegatedMethod Tree Map", e);
				}
			}
		}
		
		//Ensure exactly one default delegated Method
		for(Entry<String, List<RoutedMethodDefinition>> delegatedMethodImplementations: delegatedMethodsTree.entrySet()) {
			if(delegatedMethodImplementations.getValue().stream().filter(routedMethodDefinition-> routedMethodDefinition.isDefault()).count() != 1) {
				throw new FlowedRoutingConfigurationException("Exactly One isDefault Routed Component must be provided for method "+ delegatedMethodImplementations.getKey());
			};
			//Set default implementation on the final option of the list
			Collections.sort(delegatedMethodImplementations.getValue(), new Comparator<RoutedMethodDefinition>() {
			    @Override
			    public int compare(RoutedMethodDefinition a, RoutedMethodDefinition b) {
			        return !a.isDefault() && b.isDefault() ? -1 : 0;
			    }
			});
		}
		
	}
	
	
	protected Object findDelegate(Method method, MethodInvocation invocation) {
		Object beanDelegateSelected = null;
		List<RoutedMethodDefinition> methodImplementationCandidates = delegatedMethodsTree.get(method.toString());
		Iterator<RoutedMethodDefinition> it =  methodImplementationCandidates.iterator();
		while(beanDelegateSelected==null && it.hasNext()) {
			RoutedMethodDefinition routedMethodDefinition = it.next();
			
			if(routedMethodDefinition.isDefault()) {
				beanDelegateSelected = 	routedMethodDefinition.getBean();
			}else {
				System.out.println("no es el default");
				Evaluator evaluator = getEvaluator(routedMethodDefinition.getFlowCondition().value());
				boolean accepted = evaluator.evaluate(routedMethodDefinition, invocation);
				if(accepted) {
					beanDelegateSelected = routedMethodDefinition.getBean();  //Found
				}
			}
		}

		
		return beanDelegateSelected;
	}
	
	
	protected Evaluator getEvaluator(String evaluatorType) {
		Evaluator evaluator = evaluatorImplementations.get(evaluatorType);
		
		return evaluator;
	}
}

