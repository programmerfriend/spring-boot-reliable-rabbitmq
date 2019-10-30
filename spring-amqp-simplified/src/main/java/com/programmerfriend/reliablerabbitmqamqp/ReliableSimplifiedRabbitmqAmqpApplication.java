package com.programmerfriend.reliablerabbitmqamqp;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReliableSimplifiedRabbitmqAmqpApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ReliableSimplifiedRabbitmqAmqpApplication.class, args);
    }

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void run(String... args) throws Exception {
        do {
            rabbitTemplate.convertAndSend("tutorial-exchange", "primaryRoutingKey", "Hello, world!");
            Thread.sleep(60000);
        } while (true);
    }

}
