# spring-boot-reliable-rabbitmq
A repository to explore possibilities for reliable messaging and execution with Spring Boot and RabbitMQ

## Content:

1. **spring-amqp-retry** contains an example project using the built-in retry-mechanism of spring-amqp.
1. **spring-amqp-manual** contains an example project using spring-amqp and building the retry mechanismn on top of it.</br>
It uses multiple queues to realize a retry concept (see below image).

<img src="https://user-images.githubusercontent.com/5188694/67991687-596b0780-fc3a-11e9-8aac-5adb30cfced9.png" />
