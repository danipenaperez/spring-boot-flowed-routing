mvn clean install spring-boot:run -Dspring-boot.run.profiles=local -Dmaven.test.skip=true


mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=8000" -Dspring-boot.run.profiles=local