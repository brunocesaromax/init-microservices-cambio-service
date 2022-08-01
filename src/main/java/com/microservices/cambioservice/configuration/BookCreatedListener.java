package com.microservices.cambioservice.configuration;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookCreatedListener {

    @RabbitListener(queues = "book-service.v1.create-book")
    public void onBookCreated(Long bookId) {
        System.out.println("BookId recebido: " + bookId);
    }
}
