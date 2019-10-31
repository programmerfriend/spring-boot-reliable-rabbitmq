package com.programmerfriend.reliablerabbitmqamqp;

import static com.programmerfriend.reliablerabbitmqamqp.config.RabbitConfiguration.PARKINGLOT_QUEUE;
import static com.programmerfriend.reliablerabbitmqamqp.config.RabbitConfiguration.PRIMARY_QUEUE;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RetryingRabbitListener {

    private Logger logger = LoggerFactory.getLogger(RetryingRabbitListener.class);

    private RabbitTemplate rabbitTemplate;

    public RetryingRabbitListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = PRIMARY_QUEUE)
    public void primary(Message in) throws Exception {
        logger.info("Message read from workerQueue : " + in);
        if (hasExceededRetryCount(in)) {
            putIntoParkingLot(in);
            return;
        }
        throw new Exception("There was an error");
    }

    private boolean hasExceededRetryCount(Message in) {
        List<Map<String, ?>> xDeathHeader = in.getMessageProperties().getXDeathHeader();
        if (xDeathHeader != null && xDeathHeader.size() >= 1) {
            Long count = (Long) xDeathHeader.get(0).get("count");
            return count >= 3;
        }

        return false;
    }

    private void putIntoParkingLot(Message failedMessage) {
        logger.info("Retries exeeded putting into parking lot");
        this.rabbitTemplate.send(PARKINGLOT_QUEUE, failedMessage);
    }
}
