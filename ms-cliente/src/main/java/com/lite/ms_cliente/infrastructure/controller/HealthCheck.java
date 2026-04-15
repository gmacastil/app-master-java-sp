package com.lite.ms_cliente.infrastructure.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheck {
   
    @GetMapping("/")
    public String hc() {
        return "ok";
    }
}
