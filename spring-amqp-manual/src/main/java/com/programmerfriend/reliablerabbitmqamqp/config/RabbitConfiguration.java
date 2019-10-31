package com.programmerfriend.reliablerabbitmqamqp.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    private static final String EXCHANGE_NAME = "tutorial-exchange";
    public static final String PRIMARY_QUEUE = "primaryWorkerQueue";

    private static final String WAIT_QUEUE = PRIMARY_QUEUE + ".wait";

    public static final String PARKINGLOT_QUEUE = PRIMARY_QUEUE + ".parkingLot";

    private static final String PRIMARY_ROUTING_KEY = "primaryRoutingKey";

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    Queue primaryQueue() {
        return QueueBuilder.durable(PRIMARY_QUEUE)
            .deadLetterExchange(EXCHANGE_NAME)
            .deadLetterRoutingKey(WAIT_QUEUE)
            .build();
    }

    @Bean
    Queue waitQueue() {
        return QueueBuilder.durable(WAIT_QUEUE)
            .deadLetterExchange(EXCHANGE_NAME)
            .deadLetterRoutingKey(PRIMARY_ROUTING_KEY)
            .ttl(10000)
            .build();
    }

    @Bean
    Queue parkinglotQueue() {
        return new Queue(PARKINGLOT_QUEUE);
    }

    @Bean
    Binding primaryBinding(Queue primaryQueue, DirectExchange exchange) {
        return BindingBuilder.bind(primaryQueue).to(exchange).with(PRIMARY_ROUTING_KEY);
    }

    @Bean
    Binding waitBinding(Queue waitQueue, DirectExchange exchange){
        return BindingBuilder.bind(waitQueue).to(exchange).with(WAIT_QUEUE);
    }

    @Bean
    Binding parkingBinding(Queue parkinglotQueue, DirectExchange exchange) {
        return BindingBuilder.bind(parkinglotQueue).to(exchange).with(PARKINGLOT_QUEUE);
    }

}
