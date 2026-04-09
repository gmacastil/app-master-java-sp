package com.lite.ms_factura.infrastructure.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealtCheck {
   
    @GetMapping("/")
    public String hc() {
        return "ok";
    }
  
}
