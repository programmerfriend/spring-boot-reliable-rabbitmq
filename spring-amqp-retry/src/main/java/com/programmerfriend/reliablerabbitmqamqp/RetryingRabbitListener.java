package com.programmerfriend.reliablerabbitmqamqp;

import static com.programmerfriend.reliablerabbitmqamqp.config.RabbitConfiguration.PRIMARY_QUEUE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RetryingRabbitListener {

    private Logger logger = LoggerFactory.getLogger(RetryingRabbitListener.class);

    public RetryingRabbitListener() {
    }

    @RabbitListener(queues = PRIMARY_QUEUE)
    public void primary(Message in) throws Exception {
        logger.info("Message read from testq : " + in);
        throw new Exception("There was an error");
    }

}
