package br.tec.omny.auth.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    @Value("${mq.queue.site-creation}")
    private String queueName;
    
    @Value("${mq.exchange.site-creation}")
    private String exchangeName;
    
    @Value("${mq.routing-key.site-creation}")
    private String routingKey;
    
    @Bean
    public Queue siteCreationQueue() {
        return QueueBuilder.durable(queueName).build();
    }
    
    @Bean
    public DirectExchange siteCreationExchange() {
        return new DirectExchange(exchangeName);
    }
    
    @Bean
    public Binding siteCreationBinding() {
        return BindingBuilder
                .bind(siteCreationQueue())
                .to(siteCreationExchange())
                .with(routingKey);
    }
}
