package com.pedidos360.ms_rabbitmq_admin.controller;

import com.pedidos360.ms_rabbitmq_admin.dto.BindingRequest;
import com.pedidos360.ms_rabbitmq_admin.dto.ExchangeRequest;
import com.pedidos360.ms_rabbitmq_admin.dto.QueueRequest;
import com.pedidos360.ms_rabbitmq_admin.service.RabbitAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class RabbitAdminController {

    private final RabbitAdminService adminService;

    public RabbitAdminController(RabbitAdminService adminService) {
        this.adminService = adminService;
    }

    // --- ENDPOINTS COLAS ---
    @PostMapping("/queues")
    public ResponseEntity<Map<String, String>> createQueue(@Valid @RequestBody QueueRequest request) {
        String message = adminService.createQueue(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", message));
    }

    @DeleteMapping("/queues/{name}")
    public ResponseEntity<Map<String, Object>> deleteQueue(@PathVariable String name) {
        boolean deleted = adminService.deleteQueue(name);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Cola '" + name + "' eliminada con éxito", "deleted", true));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "La cola '" + name + "' no fue encontrada", "deleted", false));
    }

    // --- ENDPOINTS EXCHANGES ---
    @PostMapping("/exchanges")
    public ResponseEntity<Map<String, String>> createExchange(@Valid @RequestBody ExchangeRequest request) {
        String message = adminService.createExchange(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", message));
    }

    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<Map<String, Object>> deleteExchange(@PathVariable String name) {
        boolean deleted = adminService.deleteExchange(name);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Exchange '" + name + "' eliminado con éxito", "deleted", true));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "El exchange '" + name + "' no fue encontrado", "deleted", false));
    }

    // --- ENDPOINTS BINDINGS ---
    @PostMapping("/bindings")
    public ResponseEntity<Map<String, String>> createBinding(@Valid @RequestBody BindingRequest request) {
        String message = adminService.createBinding(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", message));
    }

    @DeleteMapping("/bindings")
    public ResponseEntity<Map<String, String>> removeBinding(@Valid @RequestBody BindingRequest request) {
        adminService.removeBinding(request);
        return ResponseEntity.ok(Map.of("message", "Binding eliminado con éxito"));
    }
}
