[![Build Status](https://travis-ci.org/NovaTecConsulting/BeanTest.svg?branch=master)](https://travis-ci.org/NovaTecConsulting/BeanTest)

# Bean Testing for Java EE Applications using CDI

This project attempts to show an interesting approach on testing Java EE
Applications. It uses a CDI Container to resolve dependencies like EJBs
or Resources when running unit test in a standard environment.

The name "Bean Testing" is used, since it isn't about proper unit tests.
However, the feedback speed is very close to unit test and the tests
look undistinguishable too.

**Main advantages:**

-   Very fast test feedback (very close to unit test feedback speed).

-   Dependencies are solved automatically without the need of a JEE
    Application Server (or Embedded Server).

-   Everything is CDI so you can easliy extend the functionality.

-   You get basic transaction propagation support.

-   You can provide your own mocks to test external dependencies.

-   You use the usual stuff for configuration: persistence.xml,
    beans.xml, Junit, etc.

**Production ready**

BeanTest is currently being used in some (big) customer projects. The projects are big Java EE Applications with several subsystems (.ear's). Each subsystem consists of several modules (.jar's) as well. We haven't faced any critical problem. Usually one can fix a problem by using standard CDI features.

**Examples**

You can find the examples about how a "Bean" test looks like under
*src/test/java* (look for classes whose names begin with Test\*)

## How to use it

*  Add the following dependency in your pom:

```xml

    <dependency>
      <groupId>info.novatec</groupId>
      <artifactId>bean-test</artifactId>
      <version>{currentVersion}</version>
      <scope>test</scope>
    </dependency>
```

*  You also need to add the following repository to your pom:

```xml
    <repository>
       <id>Novatec</id>
       <name>Novatec Repository</name>
       <url>http://repository.novatec-gmbh.de/content/repositories/novatec</url>
    </repository>
```
*  Create an empty beans.xml file under src/test/resources/META-INF

*  Create a persistence unit called "beanTestPU" in your
    persistence.xml (you can place it under src/test/resources/META-INF)

*  Write a test similar to the ones in the examples using your
    production code.

*  Depending on your project structure, you might get an
    *UnsatisfiedResolutionException* if some classes are not available
    in your classpath. You can solve this by providing a Mock (See the
    mock example for this).

## Why you should use it

First of all, this approach is neither a replacement for unit nor
integration tests. This approach is something in the middle.

You should always write unit tests for essential business logic.

You should always write integration tests to check that everything works
as expected.

So, why use this approach? Because you get the best of both worlds: You
get the speed of unit tests with almost the coverage of integration
tests and all this with minimal configuration and with standard and well
known frameworks like JPA, CDI, Mockito and Junit.

Since you don't need an application server for running your tests, you
can integrate them in your normal unit test build process. In this way,
you get almost integration test coverage in your normal builds.

### Requirements

-   JDK 6 and above.

-   Maven

### Contribute

Just fork.
