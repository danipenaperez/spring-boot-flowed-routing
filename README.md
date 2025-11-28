![screenshot](https://raw.githubusercontent.com/danipenaperez/spring-boot-flowed-routing/refs/heads/main/docs/floweRoutingLogo.png)

# spring-boot-flowed-routing
Easy Routing bean implementations based on local rules or extenal integrations

## Key Features

* Easy Flow code management execution 
* Focused on Clean Code and SOLID principles
* Easy installation
  - Integrate as Spring Boot starter in your project.
* Easy to use
  - Use simple annotations to indicate a Flowed Bean execution selection.
* Easy extendible 
  - Use default routing evaluators (Based on SpEL, with Spring context evaluations).
  - Or write your own evaluator engine simply extending the Evaluator interface.
  - Use your available beans to evaluate any flow execution.
  - Integrate thirdparty tools (such a feature flags provider) to delegate executions.

## Use Cases

* Multitenant Applications: same App different behaviours
* One trunk development: live ready and in progress features, activate or deactivate when you want
* Experimental code: Your repository is slow. Within the same code add new Repository solution and this repository only will be executed on conditions (maybe only for your current user), the other users will not be affected. Easily remove the code when experiment is finish.
* ...

## Installation

Simply add this dependency at your Spring Boot project:

```xml
		<dependency>
			<groupId>io.github.danipenaperez</groupId>
			<artifactId>spring-boot-flowed-routing-starter</artifactId>
			<version>${project.version}</version>
		</dependency>
```
No further configuration needed.

## Usage example (Simple Use case)

Imagine a very simple application that greeting a user. 

As usual create the controller:

```java
	GreetingService greetingService;
	
	@GetMapping("/greeting")
    public String serviceA(@RequestParam("userName") String userName) {
        return greetingService.greeting(userName);
    }
```

### Lets create the main GreetingService interface 

Mark the GreetingService interface using **@RoutedInterface**.

Now GreetingService interface is managed as proxied invocation. The invocation will be evaluated and delegated the execution to the target delegate bean implementations.

```java
  	@RoutedInterface
  	public interface GreetingService {

    	public String greeting(String userName);
  	}
```

### Lets create a Default interface implementation bean.

We need the default GreetingService implementation. This default implementation is the fallback so is needed to write any evaluation configuration.

Simply mark as **@RoutedComponent**.  This annotation extends @Component and the class will be recognized as Spring Bean.

To indicate that is a Default implemnentation add **isDefaultRouting=true**.

```java

@RoutedComponent(isDefaultRouting = true) 
public class DefaultGreetingService  implements GreetingService{

	@Override
	public String greeting(String userName){
		return "Greetings for "+userName;
	}

}

```

For now you application has the default Greeting service created, all request through inteface will be managed by DefaultGreetingService bean.

### Adding different implementation for the same Use Case

**After few days Product Owner wants** that the users which first username letter is "A" will receive a "you are amazing {username}" message. 

What and horrible tendencies could be here... Maybe a lot of if/else conditions, maybe mixed @autowired elementes in the same bean class.

Maybe worth like this:

```java

@RoutedComponent(isDefaultRouting = true) 
public class DefaultGreetingService  implements GreetingService{

	GrettingMessageProvider defaultGreetingProvider;
	AfisrtLetterGreetingProvider AfisrtLetterGreetingProvider;

	@Override
	public String greeting(String userName){
		String message;
		if(userName.startsWith("A")){
			message = AfisrtLetterGreetingProvider.grettingMessage(userName);
		}else{
			message = defaultGreetingProvider.grettingMessage(userName);
		}
		return message;
	}

}

```
- Each if statement need a Provider bean that is autowired, a lot of dependencies on the same bean class
- If new feature is requested, another if condition and another dependency should be added..
- If a feature is discarted is needed to remove if else condition and dependency
- ....

**The solution**

It is easy, no if/else sentences needed. Not horrible bean implementation with shared autowired dependencies in the same class.

**Maintain your code simple and clean.**

Simply create new GreetingService implementation and annote with **@RoutedComponent** . Write the SpelCondition that checks for userName first letter matchs with "A".


```java
@RoutedComponent
public class AUsersGreetingService  implements GreetingService{

	@FlowConditionType("SpEL") //Indicate use default evaluator provided in starter. Will use SpEL expressions
	@FlowSpelCondition(evaluationExpression = "#userName.startsWith('A')") 
	@Override
	public String greeting(String userName){
		return "You are amazing "+userName;
	}
}
```

At this point, when GreetingService.greeting() method is invoked, will be intercepted and evaluated the implementations **@FlowSpelCondition**.

If the AUsersGreetingService.greeting FlowSpelCondition match, this bean will do the job. If any implementation match, the @RoutedComponent(isDefaultRouting = true) will be delegated.

**Easy extendible and easy maintain**
- You can add all custom implementations for GreetingService at any time without touch the initial Default implementation, and you can remove it in the same way.
- Simply remove a feature without touch original implementation.
- It is easy add new features all in the same code in a clean way, each bean has its owns dependencies.

You can execute this code running the demo at [demos/flowed-routing-simple-demo](demos/flowed-routing-simple-demo)

# More complex Use Cases

## Example 1 : Executions based on Request Context

Imagine that your application is a multitenant application. 

You can choose a Greeting Service based on a **double factor multi evaluations**:  based on TenantId and username first letter.
 
TenantId supposed is extracted from a request header.

First create a bean that extract tenant info from Request:

```java
@Component
public class ExecutionContext {

	public String getTenantName () {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		return request.getHeader("tenantId");
	}
}
```

Now simply change *AUsersGreetingService* to include ExecutionContext.getTenantName at evaluation

The evaluation Expression will look like this:
```java
evaluationExpression = "#username.startsWith('A') && @executionContext.getTenantName() == 'tenant_1'"
```

The finally code likes that, and this execution will be only executed for users that first username letter is "A" and owns to tenant_1.  Other invocations will be managed by DefaultGreetingService implementation (the fallback).

```java
@RoutedComponent
public class AUsersGreetingService  implements GreetingService{

	@FlowConditionType("SpEL") //Indicate use default evaluator provided in starter. Will use SpEL expressions
	@FlowSpelCondition(evaluationExpression = "#userName.startsWith('A') 
												&& @executionContext.getTenantName() == 'tenant_1'") 
	@Override
	public String greeting(String userName){
		return "You are amazing "+userName;
	}
}
```

See the demo at [demos/flowed-routing-simple-demo](demos/flowed-routing-simple-demo)


## Example 2: Executions based on database flags

At this point you will think about dynamic enable or disable bean availability at runtime.

> [!NOTE]
> Imagine that your app search is slow and wants to use other repository implementation, but without affects the current. You can create your new repository implementation and activate only for your invocations. (Experimental features)

Image the table **flags**

| feature          | enabled |
|------------------|---------|
| greeting_tenant1 | true    |
|                  |         |

and the @Repository

```java
@Repository
public interface FlagsRepository extends JpaRepository<Flag, Long> {

    @Query("SELECT t.enabled FROM Flag f WHERE t.feature=?1")
    Boolean isFlagEnabled(String flagName);

}
```

And a @Component that encapsulate the repository calls.

```java
@Component
@AllArgsConstructor
public class FlagService {
	FlagRepository flagRepository;

public boolean isFlagActive(String flagName) {
		return flagRepository.isFlagEnabled(flagName);
	}
}
```

Now update your evaluation using the FlagService bean available at context, and making a **triple condition based on database value + Request Value + input method value**.

The code will look like this:

```java
@RoutedComponent
public class AUsersGreetingService  implements GreetingService{

	@FlowConditionType("SpEL") //Indicate use default evaluator provided in starter. Will use SpEL expressions
	@FlowSpelCondition(evaluationExpression = " @flagService.isFlagEnabled('greeting_tenant1)
                                              && #username.startsWith('A') 
                                              && @executionContext.getTenantName() == 'tenant_1'") 
	@Override
	public String greeting(String username){
		return "You are amazing "+username;
	}
}
```

See the demo at [demos/flowed-routing-database-flag-demo](demos/flowed-routing-database-flag-demo)

## Example 3 : Executions based on third party services 

Maybe only binary (true/false) database flag management is not enough and wants to integrate with specified feature flags services that offers further configuration (rollout, percentage, context evaluations, etc...)

If you are newer at FeatureFlags solutions, please visit [https://openfeature.dev/docs/reference/intro](https://openfeature.dev/docs/reference/intro) to know about benefits about.

If you want to delegate the execution based on external Feature Flags Services, you can integrate with third party services.

A simple easy to manage openFeature Spec implementation is the opensource project [GoFeatureFlags](https://gofeatureflag.org/) 

See the demo using locally [GoFeatureFlags](https://gofeatureflag.org/) at [demos/flowed-routing-openfeature-integration-demo](demos/flowed-routing-openfeature-integration-demo)


# CUSTOM EVALUATORS

At this point we were using the default evaluator provided in flowed-routing-core, that manage the evaluationExpression as a SpEL expression.  

```java
@EvaluatorType("SpEL")
public class SpELEvaluator implements Evaluator{

```

But maybe you want to create a custom evaluator that manage evaluationExpression in other way.

Simply write a Evaluator implemetation and indicate a **@EvaluatorType** name.

This is a simple evaluator that uses klingon language [https://www.translator.eu/espanol/klingon/traductor/](https://www.translator.eu/espanol/klingon/traductor/)

First create the Klingon expression annotation

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface KlingonCondition {
	String klingonExpression();
}

```

And the evaluator for KlingonCondition
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

Now configure @RoutedComponent to use the klingon evaluator type:

```java
@RoutedComponent
public class AUsersGreetingService  implements GreetingService{

	@FlowConditionType("klingon") //Indicate use klingon evaluator
	@KlingonCondition(klingonExpression = "Qapchu' functionality") 
	@Override
	public String greeting(String username){
		return "You are amazing "+username;
	}
}
```

See the demo at [demos/flowed-routing-custom-evaluator-demo](demos/flowed-routing-custom-evaluator-demo)

# Best practices

If you are using modular application builder, such as maven or gradle it is a good idea to create new Feature implementations in different jars and use maven Profiles to build the final artefact easily.

If the product Owner wants to delete or add features simply use maven profile to include/exclude the jar that contains the feature implementation and all third-party dependencies.


## Emailware

Spring Boot Flowed Routing is Free to extend and usage. I'd like you send me an email at <danipenaperez@gmail.com> about anything you'd want to say about this software. I'd really appreciate it!

## Support

If you like this project and think it has helped in any way, consider buying me a coffee!
<script type="text/javascript" src="https://cdnjs.buymeacoffee.com/1.0.0/button.prod.min.js" data-name="bmc-button" data-slug="danipenaperez" data-color="#FFDD00" data-emoji=""  data-font="Cookie" data-text="Buy me a coffee" data-outline-color="#000000" data-font-color="#000000" data-coffee-color="#ffffff" ></script>

## License

Apache License 2.0
