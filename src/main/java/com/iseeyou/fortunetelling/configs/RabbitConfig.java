package com.iseeyou.fortunetelling.configs;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    // ===================== NOTIFICATION EXCHANGE & QUEUE =====================
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange("notification.exchange", true, false);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable("notification.queue")
                .withArgument("x-dead-letter-exchange", "notification.dlx")
                .build();
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue)
                .to(notificationExchange)
                .with("notification.send");
    }

    // ===================== USER MANAGEMENT EXCHANGE & QUEUES =====================
    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange("user.exchange", true, false);
    }

    // User Login Queue
    @Bean
    public Queue userLoginQueue() {
        return QueueBuilder.durable("user.login.queue")
                .withArgument("x-dead-letter-exchange", "user.dlx")
                .build();
    }

    @Bean
    public Binding userLoginBinding(Queue userLoginQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userLoginQueue)
                .to(userExchange)
                .with("user.login");
    }

    // User Logout Queue
    @Bean
    public Queue userLogoutQueue() {
        return QueueBuilder.durable("user.logout.queue")
                .withArgument("x-dead-letter-exchange", "user.dlx")
                .build();
    }

    @Bean
    public Binding userLogoutBinding(Queue userLogoutQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userLogoutQueue)
                .to(userExchange)
                .with("user.logout");
    }

    // User Delete Queue
    @Bean
    public Queue userDeleteQueue() {
        return QueueBuilder.durable("user.delete.queue")
                .withArgument("x-dead-letter-exchange", "user.dlx")
                .build();
    }

    @Bean
    public Binding userDeleteBinding(Queue userDeleteQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userDeleteQueue)
                .to(userExchange)
                .with("user.delete");
    }

    // ===================== MESSAGE CONVERTER =====================
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}