package com.microservices.cambioservice.configuration;

import com.microservices.cambioservice.dto.BookRetrieve;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DeadLetterQueueListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String X_RETRY_HEADER = "x-dlq-retry";
    private static final String DLQ = "book-service.v1.create-book.dlx.cambio-service.dlq";
    private static final String DLQ_PARKING_LOT = "book-service.v1.create-book.dlx.cambio-service.dlq.parking-lot";
    private static final int LIMIT_RETRIES = 3;

    @RabbitListener(queues = DLQ)
    public void process(BookRetrieve bookRetrieve, @Headers Map<String, Object> headers) {
        Integer retryHeader = (Integer) headers.get(X_RETRY_HEADER);

        if (retryHeader == null) retryHeader = 0;

        System.out.println("Reprocessando criação de livro de id: " + bookRetrieve.getId());

        if (retryHeader < LIMIT_RETRIES) {
            //Reprocessing
            Map<String, Object> updatedHeaders = updateHeaders(headers, retryHeader);

            final MessagePostProcessor messagePostProcessor = message -> {
                MessageProperties messageProperties = message.getMessageProperties();
                updatedHeaders.forEach(messageProperties::setHeader);
                return message;
            };

            System.out.println("Reenviando criação de livro de id: " + bookRetrieve.getId() + " para a DLQ");
            this.rabbitTemplate.convertAndSend(DLQ, bookRetrieve, messagePostProcessor);

        } else {
            sendMessageToParkingLot(bookRetrieve);
        }
    }

    private Map<String, Object> updateHeaders(Map<String, Object> headers, Integer retryHeader) {
        Map<String, Object> updatedHeaders = new HashMap<>(headers);

        int tryCount = retryHeader + 1;
        updatedHeaders.put(X_RETRY_HEADER, tryCount);

        return updatedHeaders;
    }

    private void sendMessageToParkingLot(BookRetrieve bookRetrieve) {
        System.out.println("Reprocessamento falhou, enviando criação de livro de id: " + bookRetrieve.getId() + " para o parking lot");
        this.rabbitTemplate.convertAndSend(DLQ_PARKING_LOT, bookRetrieve);
    }

}
