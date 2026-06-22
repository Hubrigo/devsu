package com.devsu.clientes.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-keys.created}")
    private String routingKeyCreated;

    @Value("${rabbitmq.routing-keys.updated}")
    private String routingKeyUpdated;

    @Value("${rabbitmq.routing-keys.deleted}")
    private String routingKeyDeleted;

    public void publishCreated(ClienteEvent event) {
        publish(routingKeyCreated, event);
    }

    public void publishUpdated(ClienteEvent event) {
        publish(routingKeyUpdated, event);
    }

    public void publishDeleted(ClienteEvent event) {
        publish(routingKeyDeleted, event);
    }

    private void publish(String routingKey, ClienteEvent event) {
        log.info("Publicando evento [{}] para clienteId={}", event.getEventType(), event.getClienteId());
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}
