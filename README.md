![screenshot](https://raw.githubusercontent.com/danipenaperez/spring-boot-flowed-routing/refs/heads/main/docs/floweRoutingLogo.png)

# spring-boot-flowed-routing
Easy Routing bean implementations based on local rules or extenal integrations

## Key Features

* Easy Flow code management execution 
  - Instantly see what your Markdown documents look like in HTML as you create them.
* Easy integration
  - Integrate as Spring Boot starter in your project.
* Easy use
  - Use simple annotations to indicate a Flowed execution selection.
* Easy extendible 
  - Use default routing evaluators (Based on SpEL, with Spring context evaluations).
  - Or write your own evaluator engine simply extending the Evaluator interface.
  - Use your available beans to evaluate any flow execution.
  - Integrate thirdparty tools (such a feature flags provider) to delegate executions.

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

## Usage example

Imagine a very simple application that greeting a user. 

Using the controller:

```java
	 
	 GreetingService greetingService;
	 
	 @GetMapping("/greeting")
    public String serviceA(@RequestParam("userName") String userName) {
        return greetingService.greeting(userName);
    }
```

Lets create the GreetingService interface and mark as **@RoutedInterface**.

**@RoutedInterface** this interface will be proxied to evaluate which implementations will delegate the execution.

```java
	@RoutedInterface
  public interface GreetingService {

    public String greeting(String userName);
  }
```

For now you application has the default Greeting service created:

**@RoutedComponent** extends @Component and will be registered as bean at context.
**isDefaultRouting=true** Indicate that is the default flow execution.

```java

@RoutedComponent(isDefaultRouting = true) 
public class DefaultGreetingService  implements GreetingService{

	@Override
	public String greeting(String userName){
		return "Greetings for "+userName;
	}

}

```

**After few days Product Owner wants** that Greeting message for users that first username letter is "A" will receive a "you are amazing {username}" message. 

It is easy, simple create new GreetingService implementation with **@RoutedComponent** and write a SpelCondition that checks for userName first letter matchs with "A".

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

At this point, when GreetingService.greeting() method is invoked, will be intercepted and evaluate the implementations **@FlowSpelCondition** if one match will be delegated the execution, if any implementation match, the @RoutedComponent(isDefaultRouting = true) will be delegated.

You can add all custom implementations for GreetingService at any time without touch the initial Default implementation. And you can remove it in the same way.

You can execute this code running the demo at [demos/flowed-routing-simple-demo](demos/flowed-routing-simple-demo)

# More complex examples

## Example 1 : executions based on Request Context

Imagine that your application is a multitenant application. You can choose a Greeting Service based on multi evaluations:  TenantId  and username first letter.
 
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

Now simply change *AUsersGreetingService* to include ExecutionContext.getTenantName at evaluation:
```java
evaluationExpression = "#username.startsWith('A') && @executionContext.getTenantName() == 'tenant_1'"
```

The finally code likes that, and this execution will be only executed for users that first username letter is "A" and owns to tenant_1.  Other invocations will be managed by DefaultGreetingService implementation.

```java
@RoutedComponent
public class AUsersGreetingService  implements GreetingService{

	@FlowConditionType("SpEL") //Indicate use default evaluator provided in starter. Will use SpEL expressions
	@FlowSpelCondition(evaluationExpression = "#userName.startsWith('A') && @executionContext.getTenantName() == 'tenant_1'") 
	@Override
	public String greeting(String userName){
		return "You are amazing "+userName;
	}
}
```

## Example 2: executions based on database flags

Maybe you want to enable and disable execution flows based on dynamic flags. Image the table **flags**

| feature          | enabled |
|------------------|---------|
| greeting_tenant1 | false   |
| greeting_tenant2 | true    |
|                  |         |

and the @Repository

```java
@Repository
public interface FlagsRepository extends JpaRepository<Flag, Long> {

    @Query("SELECT t.enabled FROM Flag f WHERE t.feature=?1")
    Boolean isFlagEnabled(String flagName);

}
```

Now update your evaluation using the FlagsRepository bean available at context, and making a **triple condition based on database value + Request Value + input method value**.

```java
@RoutedComponent
public class AUsersGreetingService  implements GreetingService{

	@FlowConditionType("SpEL") //Indicate use default evaluator provided in starter. Will use SpEL expressions
	@FlowSpelCondition(evaluationExpression = " @FlagsRepository.isFlagEnabled('greeting_tenant1)
                                              && #username.startsWith('A') 
                                              && @executionContext.getTenantName() == 'tenant_1'") 
	@Override
	public String greeting(String username){
		return "You are amazing "+username;
	}
}
```

## Example 2 : executions based on third party services 

If you want to delegate the execution based on flags, you can integrate with third party services.

Example using open source Feature flags provider **https://github.com/flipt-io/flipt**

See flipt demo at demos/flowed-routing-flipt-io-demo


# CUSTOM EVALUATORS

At this point we were using the default evaluator provided in flowed-routing-core, that manage the evaluationExpression as a SpEL expression.  

```java
@EvaluatorType("SpEL")
public class SpELEvaluator implements Evaluator{

```

But maybe you want to create a custom evaluator that manage evaluationExpression in other way.

Simply write a Evaluator implemetation and indicate a @EvaluatorType name.

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
		
    if("Qapchu' functionality".equals(KlingonCondition.wheaterCklingonExpressionondition())){
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



# Best practices

If you are using modular application builder, such as maven or gradle it is a good idea to create new Feature implementations in different jars and use maven Profiles to build the final artefact easily.

If the product Owner wants to delete or add features simply use maven profile to include the jar that contains the feature implementation. 

See /demos/flowed-routing-building-demo project.




## Emailware

Spring Boot Flowed Routing is Free to extend and usage. I'd like you send me an email at <danipenaperez@gmail.com> about anything you'd want to say about this software. I'd really appreciate it!

## Credits

This software uses the following open source packages:

- [Electron](http://electron.atom.io/)
- [Node.js](https://nodejs.org/)
- [Marked - a markdown parser](https://github.com/chjj/marked)
- [showdown](http://showdownjs.github.io/showdown/)
- [CodeMirror](http://codemirror.net/)
- Emojis are taken from [here](https://github.com/arvida/emoji-cheat-sheet.com)
- [highlight.js](https://highlightjs.org/)

## Related

[Try Web version of Markdownify](https://notepad.js.org/markdown-editor/)

## Support

If you like this project and think it has helped in any way, consider buying me a coffee!

<script type="text/javascript" src="https://cdnjs.buymeacoffee.com/1.0.0/button.prod.min.js" data-name="bmc-button" data-slug="danipenaperez" data-color="#FFDD00" data-emoji=""  data-font="Cookie" data-text="Buy me a coffee" data-outline-color="#000000" data-font-color="#000000" data-coffee-color="#ffffff" ></script>


## License

MIT

---

> [amitmerchant.com](https://www.amitmerchant.com) &nbsp;&middot;&nbsp;
> GitHub [@amitmerchant1990](https://github.com/amitmerchant1990) &nbsp;&middot;&nbsp;
> Twitter [@amit_merchant](https://twitter.com/amit_merchant)

