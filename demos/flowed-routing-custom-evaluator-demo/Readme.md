# Spring boot Flowed Custom Evaluator


## Introduction

By default spring-boot-flowed-routing starter provides a Default Evaluator that is based on [SpEL](https://docs.spring.io/spring-framework/docs/3.0.x/reference/expressions.html) and bean Context Evaluations.

```java
@EvaluatorType("SpEL")
public class SpELEvaluator implements Evaluator{

```
# Creating Custom Evaluators

Simply write a Evaluator implemetation and indicate a **@EvaluatorType** name.

This is a simple evaluator implementation that uses klingon language [https://www.translator.eu/espanol/klingon/traductor/](https://www.translator.eu/espanol/klingon/traductor/)

## 1. Create the Condition Evaluation Annotation

First create the Klingon expression annotation:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface KlingonCondition {
	String klingonExpression();
}

```
## 2. Create the Evaluator 

Implements **Evaluator** and use the **KlingonCondition** method annotation to get metadata to perform the evaluation.

The evaluator for KlingonCondition willl look like this:

```java
@Component
@EvaluatorType(value = "klingon")
@Slf4j
public class klingonEvaluator implements Evaluator{

	
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

```

## 3. Use the Evaluator on your @RoutedComponent

Now configure @RoutedComponent to use the klingon evaluator at the method signature.

```java
@RoutedComponent
public class NewFeaturedGreetingService  implements GreetingService{

	@FlowConditionType("klingon") //Indicate use klingon evaluator
	@KlingonCondition(klingonExpression = "Qapchu' functionality") 
	@Override
	public String greeting(String username){
		return "New Greeting Service is enabled for you "+username+ " [klingon accepted request]";
	}
}
```

## 3. Run the application

Simply Spring boot run:
```sh
mvn clean install spring-boot:run 
```

## Execute the test

Because the evaluator always match with "Qapchu' functionality", all executions will be managed by 
NewFeaturedGreetingService.

```sh
curl --location --request GET 'http://localhost:8080/greeting?userName=Anthony'
New Greeting Service is enabled for you Anthony [klingon accepted request]
```