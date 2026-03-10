# spring-boot-flowed-routing

## Project Overview
A Spring Boot custom starter library that implements dynamic method routing — directing method calls to different bean implementations at runtime based on configurable conditions, without changing callers.

- **GroupId:** `io.github.danipenaperez`
- **Version:** `0.1.0`
- **Java:** 21
- **Spring Boot:** 3.5.4 (root), 3.5.5 (demos)

## Module Structure

```
spring-boot-flowed-routing/               ← Root aggregator POM
├── flowed-routing-core/                  ← Core library (annotations, interceptors, evaluators)
├── spring-boot-flowed-routing-starter-parent/
│   ├── spring-boot-flowed-routing-starter/             ← Thin starter (no code, pulls in autoconfigure)
│   └── spring-boot-flowed-routing-starter-autoconfigure/  ← Spring auto-configuration & proxy wiring
└── demos/
    ├── flowed-routing-simple-demo/
    ├── flowed-routing-simple-tenant-demo/
    ├── flowed-routing-database-flag-demo/
    ├── flowed-routing-openfeature-integration-demo/
    └── flowed-routing-custom-evaluator-demo/
```

## Maven Profiles

| Profile | Modules | Purpose |
|---|---|---|
| `default` | all | Local development, includes demos |
| `ci-cd` | core + starter only | Maven Central release (GPG sign, javadoc, sources) |
| `quality` | core + starter only | Static analysis with SpotBugs |

```bash
mvn clean install                     # default profile
mvn clean install -P quality          # run SpotBugs analysis
mvn verify -P ci-cd                   # release build
```

## Key Concepts

### Annotations
- `@RoutedInterface` — marks an interface whose calls are routed (implies `@Component @Primary`)
- `@RoutedComponent(isDefaultRouting=true/false)` — marks an implementing bean; exactly one must be `isDefaultRouting=true` as fallback
- `@FlowConditionType("SpEL")` — declares which evaluator to use on a method
- `@FlowSpelCondition(evaluationExpression="...")` — SpEL expression evaluated at runtime
- `@EvaluatorType("name")` — tags a custom `Evaluator` bean with a string key

### How Routing Works
1. `@RoutedInterface` is detected by `FlowedRoutingClassPathBeanDefinitionScanner`
2. A `FactoryBean` (`FlowedRoutingProxiedInterfaceBean`) registers an AOP proxy for it
3. On `ApplicationStartedEvent`, `FlowedRoutingComponentProxyFactory` wires all `@RoutedComponent` beans and `Evaluator` beans into each interceptor
4. At call time, `FlowRoutingInterceptor` iterates candidates: first matching evaluator wins, default bean is the fallback

### Built-in Evaluator: SpEL
- Key: `"SpEL"`, class: `SpELEvaluator`
- Method parameters are available as SpEL variables by name (e.g. `#userName`)
- Full Spring bean access via `@beanName` syntax

## Important Conventions

### Demo modules
- All demos use `spring-boot-starter-parent` as parent (NOT the project root POM)
- Therefore each demo must explicitly declare `lombok` dependency + `maven-compiler-plugin` with Lombok `annotationProcessorPaths`

### SpotBugs
- Config file: `spotbugs-exclude.xml` at project root
- Excludes: Lombok false positives, Spring `@Autowired` field warnings, demo packages
- Plugin version property: `${spotbugs.version}` (currently `4.9.8.2`)
- Run with: `mvn verify -P quality`
- Reports generated at `target/spotbugsXml.xml` and `target/spotbugs.html` per module

### Exception types
- `FlowedRoutingConfigurationException` — startup/configuration errors (checked)
- `FlowedRoutingRuntimeException` — runtime errors during routing (unchecked)

## Key Files

| File | Purpose |
|---|---|
| `flowed-routing-core/src/main/java/.../interceptor/FlowRoutingInterceptor.java` | Core routing engine, builds method→delegates tree |
| `flowed-routing-core/src/main/java/.../spel/SpELEvaluator.java` | Built-in SpEL evaluator |
| `starter-autoconfigure/.../context/FlowedRoutingComponentProxyFactory.java` | Wires beans into interceptors on startup |
| `starter-autoconfigure/.../context/FlowedRoutingBeanRegistrar.java` | Classpath scanner registrar |
| `spotbugs-exclude.xml` | SpotBugs exclusion filter |
