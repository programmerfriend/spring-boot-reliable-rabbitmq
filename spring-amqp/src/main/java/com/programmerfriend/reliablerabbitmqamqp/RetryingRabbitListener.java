package com.programmerfriend.reliablerabbitmqamqp;

import static com.programmerfriend.reliablerabbitmqamqp.config.RabbitConfiguration.EXCHANGE_NAME;
import static com.programmerfriend.reliablerabbitmqamqp.config.RabbitConfiguration.PARKINGLOT_QUEUE;
import static com.programmerfriend.reliablerabbitmqamqp.config.RabbitConfiguration.PRIMARY_QUEUE;
import static com.programmerfriend.reliablerabbitmqamqp.config.RabbitConfiguration.RETRY_QUEUE;
import static com.programmerfriend.reliablerabbitmqamqp.config.RabbitConfiguration.WAIT_QUEUE;
import static com.programmerfriend.reliablerabbitmqamqp.config.RabbitConfiguration.X_RETRIES_HEADER;

import java.util.Map;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RetryingRabbitListener {

    private RabbitTemplate rabbitTemplate;

    public RetryingRabbitListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = PRIMARY_QUEUE)
    public void primary(String in) throws Exception {
        System.out.println("Message read from testq : " + in);
        throw new AmqpRejectAndDontRequeueException("There was an error");
    }

    @RabbitListener(queues = RETRY_QUEUE)
    public void republish(Message failedMessage) {
        System.out.println("Message read from RetryQueue : " + failedMessage);
        Map<String, Object> headers = failedMessage.getMessageProperties().getHeaders();
        Integer retriesHeader = (Integer) headers.get(X_RETRIES_HEADER);

        if (retriesHeader == null) {
            retriesHeader = 0;
        }
        if (retriesHeader < 3) {

            try {
                //assume here something real
                throw new Exception("There was an error handling the message");
            } catch (Exception e) {
                headers.put(X_RETRIES_HEADER, retriesHeader + 1);
                System.out.println("Doing something resulting in an error again {error-count: " + retriesHeader + "}");
                this.rabbitTemplate.send(EXCHANGE_NAME, WAIT_QUEUE, failedMessage);
            }
        } else {
            putIntoParkingLot(failedMessage);
        }
    }

    private void putIntoParkingLot(Message failedMessage) {
        System.out.println("Retries exeeded putting into parking lot");
        this.rabbitTemplate.send(PARKINGLOT_QUEUE, failedMessage);
    }
}
