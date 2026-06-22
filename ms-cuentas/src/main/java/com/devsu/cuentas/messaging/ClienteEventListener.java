package com.devsu.cuentas.messaging;

import com.devsu.cuentas.entity.ClienteCache;
import com.devsu.cuentas.repository.ClienteCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteEventListener {

    private final ClienteCacheRepository clienteCacheRepository;

    @RabbitListener(queues = "${rabbitmq.queue}")
    @Transactional
    public void handleClienteEvent(ClienteEvent event) {
        log.info("Evento recibido [{}] para clienteId={}", event.getEventType(), event.getId());

        switch (event.getEventType()) {
            case "CREATED", "UPDATED" -> upsertCache(event);
            case "DELETED" -> markInactive(event);
            default -> log.warn("Evento desconocido: {}", event.getEventType());
        }
    }

    private void upsertCache(ClienteEvent event) {
        ClienteCache cache = clienteCacheRepository.findById(event.getId())
                .orElse(ClienteCache.builder().clienteId(event.getId()).build());
        cache.setNombre(event.getNombre());
        cache.setEstado(event.getEstado());
        cache.setUpdatedAt(LocalDateTime.now());
        clienteCacheRepository.save(cache);
        log.info("Cache actualizado para clienteId={}", event.getId());
    }

    private void markInactive(ClienteEvent event) {
        clienteCacheRepository.findById(event.getId()).ifPresent(cache -> {
            cache.setEstado(false);
            cache.setUpdatedAt(LocalDateTime.now());
            clienteCacheRepository.save(cache);
            log.info("Cliente id={} marcado inactivo en cache", event.getId());
        });
    }
}
