package com.lite.ms_notifica.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lite.ms_notifica.model.Notificacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    @Value("${kafka.topic.notifications}")
    private String topicName;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public void sendNotification(Notificacion notificacion) {
        try {
            String notificacionJson = objectMapper.writeValueAsString(notificacion);

            CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(topicName, notificacion.getId(), notificacionJson);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Mensaje enviado exitosamente: [{}] con offset: [{}]",
                        notificacion.getId(),
                        result.getRecordMetadata().offset());
                } else {
                    logger.error("Error al enviar mensaje: [{}]", notificacion.getId(), ex);
                }
            });

        } catch (JsonProcessingException e) {
            logger.error("Error al serializar notificación: {}", notificacion, e);
            throw new RuntimeException("Error al procesar notificación", e);
        }
    }

    public void sendMessage(String key, String message) {
        CompletableFuture<SendResult<String, String>> future =
            kafkaTemplate.send(topicName, key, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Mensaje enviado: [{}] con offset: [{}]",
                    key,
                    result.getRecordMetadata().offset());
            } else {
                logger.error("Error al enviar mensaje: [{}]", key, ex);
            }
        });
    }
}
