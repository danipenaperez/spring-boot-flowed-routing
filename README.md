![screenshot](https://raw.githubusercontent.com/danipenaperez/spring-boot-flowed-routing/refs/heads/main/docs/floweRoutingLogo.png)

# spring-boot-flowed-routing
Easy routing bean implementations based on local rules or external integrations.

## Key Features

* Easy flow code execution management
* Focused on Clean Code and SOLID principles
* Easy installation
  - Integrate as a Spring Boot starter in your project
* Easy to use
  - Use simple annotations to select a Flowed Bean execution
* Easily extendible
  - Use default routing evaluators (based on SpEL with Spring context evaluation)
  - Or write your own evaluator by simply extending the Evaluator interface
  - Use your existing beans to evaluate any flow execution
  - Integrate third-party tools (such as a feature flags provider) to delegate executions

## Use Cases

* Multitenant applications: same app, different behaviors
* One-trunk development: live-ready and in-progress features — activate or deactivate whenever needed
* Experimental code: if your repository is slow, add a new repository implementation and execute it only under certain conditions (for example, only for a specific user). Other users are not affected. Easily remove the code when the experiment is finished.
* ...

## Installation

Simply add this dependency to your Spring Boot project:

```xml
<dependency>
  <groupId>io.github.danipenaperez</groupId>
  <artifactId>spring-boot-flowed-routing-starter</artifactId>
  <version>0.X.X</version>
</dependency>
```

No further configuration needed.

## Usage example (Simple Use Case)

Imagine a very simple application that greets a user.

As usual, create the controller:

```java
GreetingService greetingService;

@GetMapping("/greeting")
public String serviceA(@RequestParam("userName") String userName) {
    return greetingService.greeting(userName);
}
```

### Let's create the main GreetingService interface

Mark the interface with **@RoutedInterface**.

Now `GreetingService` method invocations are proxied. Each invocation will be evaluated and delegated to the corresponding bean implementation.

```java
@RoutedInterface
public interface GreetingService {
    String greeting(String userName);
}
```

### Let's create a default implementation bean

We need a default `GreetingService` implementation. This implementation acts as a fallback, so no evaluation configuration is needed.

Mark it as **@RoutedComponent**. To indicate that it is the default implementation, add **isDefaultRouting = true**.

```java
@RoutedComponent(isDefaultRouting = true)
public class DefaultGreetingService implements GreetingService {

    @Override
    public String greeting(String userName){
        return "Greetings for " + userName;
    }
}
```

At this point your application has a default greeting service. All calls through the interface will be handled by `DefaultGreetingService` unless another implementation matches a routing condition.

### Adding a different implementation for the same Use Case

After a few days, the Product Owner wants users whose username starts with "A" to receive the message:
"You are amazing {username}".

A naive approach might look like this:

```java
@RoutedComponent(isDefaultRouting = true)
public class DefaultGreetingService implements GreetingService {

    GreetingMessageProvider defaultGreetingProvider;
    AFirstLetterGreetingProvider aFirstLetterGreetingProvider;

    @Override
    public String greeting(String userName){
        if(userName.startsWith("A")){
            return aFirstLetterGreetingProvider.greetingMessage(userName);
        } else {
            return defaultGreetingProvider.greetingMessage(userName);
        }
    }
}
```

Problems with this approach:

* Each `if` requires another provider bean and increases dependencies
* New features require more `if` statements
* Removing a feature requires modifying the class again

### The solution

No need for `if/else`. No overloaded bean with too many autowired dependencies.

Keep your code simple and clean.

Create a new `GreetingService` implementation annotated with **@RoutedComponent**, and write a SpEL condition that checks if the username starts with "A".

```java
@RoutedComponent
public class AUsersGreetingService implements GreetingService {

    @FlowConditionType("SpEL")
    @FlowSpelCondition(evaluationExpression = "#userName.startsWith('A')")
    @Override
    public String greeting(String userName){
        return "You are amazing " + userName;
    }
}
```

Now, when `GreetingService.greeting()` is invoked, the framework evaluates all @FlowSpelCondition annotations.
If a condition matches, the corresponding bean executes; otherwise, the default one is used.

### Easy to extend, easy to maintain

* Add new implementations anytime without touching the default implementation
* Remove features just by deleting the bean
* Each bean contains only its own dependencies

> [!NOTE]
> Best Practice: If a new feature needs third-party libraries, keep it in a separate Maven module. Your `@RoutedComponent` beans will still be discovered if they exist on the classpath.

You can run the demo at `demos/flowed-routing-simple-demo`.

# More complex Use Cases

## Example 1: Executions based on Request Context

Imagine your application is multitenant.

You can choose a Greeting Service based on a **double-factor evaluation**: tenantId and username first letter.

Assume tenantId is extracted from a request header.

First create a bean that extracts tenant info from the request:

```java
@Component
public class ExecutionContext {

    public String getTenantName() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        return request.getHeader("tenantId");
    }
}
```

Now update `AUsersGreetingService` to include `ExecutionContext.getTenantName()` in the evaluation.

The evaluation expression will look like this:

```java
evaluationExpression = "#userName.startsWith('A') && @executionContext.getTenantName() == 'tenant_1'"
```

The final code looks like this, and this implementation will be executed only for users whose username starts with "A" and belong to `tenant_1`. Other invocations will be handled by the default implementation.

```java
@RoutedComponent
public class AUsersGreetingService implements GreetingService {

    @FlowConditionType("SpEL")
    @FlowSpelCondition(evaluationExpression = "#userName.startsWith('A') && @executionContext.getTenantName() == 'tenant_1'")
    @Override
    public String greeting(String userName){
        return "You are amazing " + userName;
    }
}
```

See the demo at `demos/flowed-routing-simple-demo`.

## Example 2: Executions based on database flags

You may want to enable or disable bean availability at runtime.

> [!NOTE]
> Imagine that your app's search is slow and you want to use another repository implementation without affecting current users. You can create a new repository implementation and activate it only for specific invocations (experimental features).

Imagine the `flags` table:

| feature          | enabled |
|------------------|---------|
| greeting_tenant1 | true    |

Repository:

```java
@Repository
public interface FlagsRepository extends JpaRepository<Flag, Long> {

    @Query("SELECT f.enabled FROM Flag f WHERE f.feature = ?1")
    Boolean isFlagEnabled(String flagName);

}
```

And a component that encapsulates the repository calls:

```java
@Component
@AllArgsConstructor
public class FlagService {
    FlagRepository flagRepository;

    public boolean isFlagActive(String flagName) {
        return Boolean.TRUE.equals(flagRepository.isFlagEnabled(flagName));
    }
}
```

Now update your evaluation using the `FlagService` bean available in the context, making a **triple condition** based on database value + request value + input parameter.

```java
@RoutedComponent
public class AUsersGreetingService implements GreetingService {

    @FlowConditionType("SpEL")
    @FlowSpelCondition(evaluationExpression = "@flagService.isFlagActive('greeting_tenant1') && #userName.startsWith('A') && @executionContext.getTenantName() == 'tenant_1'")
    @Override
    public String greeting(String userName){
        return "You are amazing " + userName;
    }
}
```

See the demo at `demos/flowed-routing-database-flag-demo`.

## Example 3: Executions based on third-party services

If binary (true/false) database flags are not enough and you want to integrate with feature flagging services that offer rollouts, percentage targeting, and richer context evaluations, you can integrate third-party feature flag services.

If you are new to feature flag solutions, visit https://openfeature.dev/docs/reference/intro to learn about the benefits.

A simple open implementation that follows OpenFeature is the open-source project GoFeatureFlags: https://gofeatureflag.org/

See the demo using local GoFeatureFlags at `demos/flowed-routing-openfeature-integration-demo`.

# CUSTOM EVALUATORS

So far we used the default evaluator provided in `flowed-routing-core`, which evaluates `evaluationExpression` as a SpEL expression:

```java
@EvaluatorType("SpEL")
public class SpELEvaluator implements Evaluator {
    ...
}
```

But you may want to create a custom evaluator that interprets `evaluationExpression` differently.

Simply implement the `Evaluator` interface and annotate it with **@EvaluatorType**.

This example shows a fictional "Klingon" evaluator.

Create the Klingon expression annotation:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface KlingonCondition {
    String klingonExpression();
}
```

And the evaluator for `KlingonCondition`:

```java
@Component
@EvaluatorType("klingon")
@Slf4j
public class KlingonEvaluator implements Evaluator {

    @Override
    public boolean evaluate(RoutedMethodDefinition routedMethodDefinition, MethodInvocation invocation) {
        log.info("Using Klingon evaluator");

        KlingonCondition klingonCondition = routedMethodDefinition.getMethod().getDeclaredAnnotation(KlingonCondition.class);
        if ("Qapchu' functionality".equals(klingonCondition.klingonExpression())) {
            return true;
        } else {
            return false;
        }
    }
}
```

Now configure `@RoutedComponent` to use the Klingon evaluator type:

```java
@RoutedComponent
public class AUsersGreetingService implements GreetingService {

    @FlowConditionType("klingon")
    @KlingonCondition(klingonExpression = "Qapchu' functionality")
    @Override
    public String greeting(String userName){
        return "You are amazing " + userName;
    }
}
```

See the demo at `demos/flowed-routing-custom-evaluator-demo`.

# Best practices

If you are using a modular build system such as Maven or Gradle, it is a good idea to create new feature implementations in separate jars and use Maven profiles to build the final artifact easily.

If the Product Owner wants to add or remove features, simply use a Maven profile to include or exclude the jar that contains the feature implementation and its third-party dependencies.

## Email

Spring Boot Flowed Routing is free to extend and use. I would appreciate any feedback — please email me at <danipenaperez@gmail.com>.

## Support

If you like this project and it has helped you, consider buying me a coffee!

<script type="text/javascript" src="https://cdnjs.buymeacoffee.com/1.0.0/button.prod.min.js" data-name="bmc-button" data-slug="danipenaperez" data-color="#FFDD00" data-emoji=""  data-font="Cookie" data-text="Buy me a coffee" data-outline-color="#000000" data-font-color="#000000" data-coffee-color="#ffffff" ></script>

## License

Apache License 2.0
