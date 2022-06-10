#RECORDING API  GUIDE LINES
A simple [Spring Boot](http://projects.spring.io/spring-boot/) application
## Requirements

For building and running the application you need:

- [JDK 11](https://www.oracle.com/tr/java/technologies/javase/jdk11-archive-downloads.html)
- [Maven 3](https://maven.apache.org)

## Running the application locally

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `com.softavail.recordingapi.RecordingApiApplication` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

##Controller Description

For Controller usage please run program and check [Swagger](http://localhost:8080/swagger-ui.html) definitions.
For sample controller request please check [postman_json](/docs/recordRequestSamples.postman_collection.json) file
#Database Usage
Program uses internal h2 database all the data base values stored internal memory of system
for database configuration please check [properties](src/main/resources/application.properties) file.
In order to configure and observe data base values please check [this](http://localhost:8080/h2-console,) link after program startup


## Copyright

Released under the Apache License 2.0. See the [LICENSE](https://github.com/codecentric/springboot-sample-app/blob/master/LICENSE) file.