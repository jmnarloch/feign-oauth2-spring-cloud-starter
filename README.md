# Spring Cloud Netflix Feign OAuth2

> A Spring Cloud Feign extension that propagates OAuth2 authorization tokes.

[![Build Status](https://travis-ci.org/jmnarloch/feign-oauth2-spring-cloud-starter.svg?branch=master)](https://travis-ci.org/jmnarloch/feign-oauth2-spring-cloud-starter)

## Features

Configures Feign to propagate the OAuth2 authorization token.

## Setup

Add the Spring Cloud starter to your project:

```xml
<dependency>
  <groupId>io.jmnarloch</groupId>
  <artifactId>feign-oauth2-spring-cloud-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Properties

The only supported property is `feign.oauth2.enabled` which allows to disable this extension. 

```
feign.oauth2.enabled=true 
```

## License

Apache 2.0