package com.microservices.cambioservice.configuration;

import com.microservices.cambioservice.dto.BookRetrieve;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookCreatedListener {

    @RabbitListener(queues = "book-service.v1.create-book.cambio-service")
    public void onBookCreated(BookRetrieve bookRetrieve) {
        System.out.println(bookRetrieve.toString());
    }

}
