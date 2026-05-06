package com.lite.ms_notifica.controller;

import com.lite.ms_notifica.model.Notificacion;
import com.lite.ms_notifica.service.KafkaProducerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final KafkaProducerService producerService;

    public NotificacionController(KafkaProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping
    public ResponseEntity<String> enviarNotificacion(@RequestBody Notificacion notificacion) {
        if (notificacion.getId() == null || notificacion.getId().isEmpty()) {
            notificacion.setId(UUID.randomUUID().toString());
        }

        producerService.sendNotification(notificacion);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body("Notificación enviada con ID: " + notificacion.getId());
    }

    @PostMapping("/simple")
    public ResponseEntity<String> enviarMensajeSimple(
            @RequestParam String key,
            @RequestParam String mensaje) {

        producerService.sendMessage(key, mensaje);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body("Mensaje enviado con key: " + key);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Servicio de notificaciones activo");
    }
}
