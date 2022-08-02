package com.microservices.cambioservice.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue createBookQueue() {
        Map<String, Object> args = new HashMap<>();

        //Se quiser enviar direto para a fila em da exchange
        //args.put("x-dead-letter-routing-key", "book-service.v1.create-book.dlx.cambio-service.dlq");
        args.put("x-dead-letter-exchange", "book-service.v1.create-book.dlx");

        return new Queue("book-service.v1.create-book.cambio-service", true, false, false, args);
    }

    @Bean
    public Binding binding() {
        Queue queue = createBookQueue();
        FanoutExchange exchange = new FanoutExchange("book-service.v1.create-book");
        return BindingBuilder.bind(queue).to(exchange);
    }

    @Bean
    public Queue createBookQueueDLQ() {
        return new Queue("book-service.v1.create-book.dlx.cambio-service.dlq");
    }

    @Bean
    public Binding bindingDLQ() {
        Queue queue = createBookQueueDLQ();
        FanoutExchange exchange = new FanoutExchange("book-service.v1.create-book.dlx");
        return BindingBuilder.bind(queue).to(exchange);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> applicationReadyEventApplicationListener(RabbitAdmin rabbitAdmin) {
        return event -> rabbitAdmin.initialize();
    }
}
