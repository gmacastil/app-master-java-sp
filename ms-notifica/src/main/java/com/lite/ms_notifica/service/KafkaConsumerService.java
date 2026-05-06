package com.lite.ms_notifica.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lite.ms_notifica.model.Notificacion;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final ObjectMapper objectMapper;

    public KafkaConsumerService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @KafkaListener(topics = "${kafka.topic.notifications}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            logger.info("Mensaje recibido - Key: [{}], Partition: [{}], Offset: [{}]",
                record.key(),
                record.partition(),
                record.offset());

            Notificacion notificacion = objectMapper.readValue(record.value(), Notificacion.class);

            logger.info("Notificación procesada: {}", notificacion);

            procesarNotificacion(notificacion);

        } catch (Exception e) {
            logger.error("Error al procesar mensaje: {}", record.value(), e);
        }
    }

    private void procesarNotificacion(Notificacion notificacion) {
        logger.info("Procesando notificación de tipo: [{}] para: [{}]",
            notificacion.getTipo(),
            notificacion.getDestinatario());

        switch (notificacion.getTipo().toUpperCase()) {
            case "EMAIL":
                enviarEmail(notificacion);
                break;
            case "SMS":
                enviarSMS(notificacion);
                break;
            case "PUSH":
                enviarPushNotification(notificacion);
                break;
            default:
                logger.warn("Tipo de notificación no soportado: {}", notificacion.getTipo());
        }
    }

    private void enviarEmail(Notificacion notificacion) {
        logger.info("Enviando EMAIL a: {} - Asunto: {}",
            notificacion.getDestinatario(),
            notificacion.getAsunto());
    }

    private void enviarSMS(Notificacion notificacion) {
        logger.info("Enviando SMS a: {} - Mensaje: {}",
            notificacion.getDestinatario(),
            notificacion.getMensaje());
    }

    private void enviarPushNotification(Notificacion notificacion) {
        logger.info("Enviando PUSH a: {} - Mensaje: {}",
            notificacion.getDestinatario(),
            notificacion.getMensaje());
    }
}
