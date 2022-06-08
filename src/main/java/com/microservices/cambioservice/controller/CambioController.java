package com.microservices.cambioservice.controller;

import com.microservices.cambioservice.model.Cambio;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("cambio-service")
public class CambioController {

    @GetMapping(value = "/{amount}/{from}/{to}")
    public Cambio getCambio(@PathVariable BigDecimal amount,
                            @PathVariable String from,
                            @PathVariable String to) {
        return new Cambio(1L, from, to, BigDecimal.ONE, BigDecimal.ONE, "PORT 8000");
    }
}
