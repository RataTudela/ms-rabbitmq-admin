package com.pedidos360.ms_rabbitmq_admin.service;


import com.pedidos360.ms_rabbitmq_admin.dto.BindingRequest;
import com.pedidos360.ms_rabbitmq_admin.dto.ExchangeRequest;
import com.pedidos360.ms_rabbitmq_admin.dto.QueueRequest;
import org.springframework.amqp.core.*;
import org.springframework.stereotype.Service;

@Service
public class RabbitAdminService {

    private final AmqpAdmin amqpAdmin;

    public RabbitAdminService(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    // --- GESTIÓN DE COLAS ---
    public String createQueue(QueueRequest request) {
        Queue queue = new Queue(
                request.getName(),
                request.isDurable(),
                request.isExclusive(),
                request.isAutoDelete(),
                request.getArguments()
        );
        String declaredName = amqpAdmin.declareQueue(queue);
        return "Cola '" + declaredName + "' creada o actualizada exitosamente";
    }

    public boolean deleteQueue(String queueName) {
        return amqpAdmin.deleteQueue(queueName);
    }

    // --- GESTIÓN DE EXCHANGES ---
    public String createExchange(ExchangeRequest request) {
        Exchange exchange;
        switch (request.getType().toLowerCase()) {
            case "direct" -> exchange = new DirectExchange(request.getName(), request.isDurable(), request.isAutoDelete());
            case "topic" -> exchange = new TopicExchange(request.getName(), request.isDurable(), request.isAutoDelete());
            case "fanout" -> exchange = new FanoutExchange(request.getName(), request.isDurable(), request.isAutoDelete());
            case "headers" -> exchange = new HeadersExchange(request.getName(), request.isDurable(), request.isAutoDelete());
            default -> throw new IllegalArgumentException("Tipo de exchange no soportado: " + request.getType());
        }
        amqpAdmin.declareExchange(exchange);
        return "Exchange '" + request.getName() + "' (" + request.getType() + ") creado exitosamente";
    }

    public boolean deleteExchange(String exchangeName) {
        return amqpAdmin.deleteExchange(exchangeName);
    }

    // --- GESTIÓN DE BINDINGS ---
    public String createBinding(BindingRequest request) {
        Binding binding = new Binding(
                request.getQueueName(),
                Binding.DestinationType.QUEUE,
                request.getExchangeName(),
                request.getRoutingKey(),
                null
        );
        amqpAdmin.declareBinding(binding);
        return "Binding creado entre Exchange '" + request.getExchangeName() + "' y Cola '" + request.getQueueName() + "'";
    }

    public void removeBinding(BindingRequest request) {
        Binding binding = new Binding(
                request.getQueueName(),
                Binding.DestinationType.QUEUE,
                request.getExchangeName(),
                request.getRoutingKey(),
                null
        );
        amqpAdmin.removeBinding(binding);
    }
}