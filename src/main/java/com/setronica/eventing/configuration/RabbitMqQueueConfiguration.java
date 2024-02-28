package com.setronica.eventing.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqQueueConfiguration {

    // Names
    private static final String paymentExchangeName = "payment-exchange";
    private static final String paymentQueueName = "payment-notifications";
    private static final String entityExchangeName = "entity-exchange";
    private static final String createQueueName = "create-queue";
    private static final String updateQueueName = "update-queue";
    private static final String deleteQueueName = "delete-queue";


    // Queues
    @Bean
    public Queue paymentQueue() {
        return new Queue(paymentQueueName);
    }
    @Bean
    public Queue createQueue() {
        return new Queue(createQueueName);
    }
    @Bean
    public Queue updateQueue() {
        return new Queue(updateQueueName);
    }
    @Bean
    public Queue deleteQueue() {
        return new Queue(deleteQueueName);
    }


    // Exchanges
    @Bean
    public TopicExchange entityExchange() {
        return new TopicExchange(entityExchangeName);
    }
    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(paymentExchangeName);
    }



    // Bindings
    @Bean
    public Binding paymentBinding(Queue paymentQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(paymentQueue).to(paymentExchange).with("payment.#");
    }
    @Bean
    public Binding createBinding(Queue createQueue, TopicExchange entityExchange) {
        return BindingBuilder.bind(createQueue).to(entityExchange).with("entity.create.#");
    }
    @Bean
    public Binding updateBinding(Queue updateQueue, TopicExchange entityExchange) {
        return BindingBuilder.bind(updateQueue).to(entityExchange).with("entity.update.#");
    }
    @Bean
    public Binding deleteBinding(Queue deleteQueue, TopicExchange entityExchange) {
        return BindingBuilder.bind(deleteQueue).to(entityExchange).with("entity.delete.#");
    }


}
