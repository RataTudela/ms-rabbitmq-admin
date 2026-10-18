Microservicio Administrador de RabbitMQ (ms-rabbitmq-admin)

Este microservicio expone una API REST construida con Spring Boot (spring-boot-starter-amqp y spring-boot-starter-validation) para la administración dinámica de colas, exchanges y bindings sobre el clúster de RabbitMQ. Incluye validación de parámetros de entrada y manejo estructurado de errores (HTTP 400).

1. Requisitos Previos y Despliegue

Asegúrar de que la red compartida de Docker esté creada y levanta el servicio con Docker Compose:

# 1. Crear red compartida de Docker (si no existe)
docker network create red-compartida

# 2. Construir y levantar el contenedor
docker compose up -d --build


Verificación de estado: El servicio estará disponible en http://localhost:8084.

2. Comandos de Prueba (PowerShell)

A. Gestión de Colas (/api/admin/queues)

1. Crear una Cola Dinámica (Éxito - 201 Created)

Invoke-RestMethod -Uri "http://localhost:8084/api/admin/queues" -Method Post -ContentType "application/json" -Body '{
  "name": "cola.evaluacion.prueba",
  "durable": true,
  "exclusive": false,
  "autoDelete": false
}'


2. Probar Validación de Parámetros (Error - 400 Bad Request)

Envía un nombre vacío para comprobar la captura del error por @NotBlank:

try {
    Invoke-RestMethod -Uri "http://localhost:8084/api/admin/queues" -Method Post -ContentType "application/json" -Body '{"name": ""}'
} catch {
    $stream = $_.Exception.Response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $reader.ReadToEnd()
}


Respuesta JSON estructurada:

{
  "status": 400,
  "error": "Error de Validación en Parámetros",
  "details": {
    "name": "El nombre de la cola no puede estar vacío"
  }
}


3. Eliminar una Cola (Éxito - 200 OK)

Invoke-RestMethod -Uri "http://localhost:8084/api/admin/queues/cola.evaluacion.prueba" -Method Delete


B. Gestión de Exchanges (/api/admin/exchanges)

1. Crear un Exchange de tipo Direct

Invoke-RestMethod -Uri "http://localhost:8084/api/admin/exchanges" -Method Post -ContentType "application/json" -Body '{
  "name": "exchange.prueba.direct",
  "type": "direct",
  "durable": true
}'


2. Crear un Exchange de tipo Topic

Invoke-RestMethod -Uri "http://localhost:8084/api/admin/exchanges" -Method Post -ContentType "application/json" -Body '{
  "name": "exchange.prueba.topic",
  "type": "topic",
  "durable": true
}'


3. Probar Tipo de Exchange Inválido (Error - 400 Bad Request)

try {
    Invoke-RestMethod -Uri "http://localhost:8084/api/admin/exchanges" -Method Post -ContentType "application/json" -Body '{
      "name": "exchange.invalido",
      "type": "tipo_no_existente"
    }'
} catch {
    $stream = $_.Exception.Response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $reader.ReadToEnd()
}


4. Eliminar un Exchange

Invoke-RestMethod -Uri "http://localhost:8084/api/admin/exchanges/exchange.prueba.direct" -Method Delete


C. Gestión de Bindings (/api/admin/bindings)

1. Crear Cola, Exchange y Vincular con Binding

# 1. Crear cola de prueba
Invoke-RestMethod -Uri "http://localhost:8084/api/admin/queues" -Method Post -ContentType "application/json" -Body '{"name": "cola.binding.test"}'

# 2. Crear exchange de prueba
Invoke-RestMethod -Uri "http://localhost:8084/api/admin/exchanges" -Method Post -ContentType "application/json" -Body '{"name": "exchange.binding.test", "type": "direct"}'

# 3. Crear el Binding
Invoke-RestMethod -Uri "http://localhost:8084/api/admin/bindings" -Method Post -ContentType "application/json" -Body '{
  "queueName": "cola.binding.test",
  "exchangeName": "exchange.binding.test",
  "routingKey": "key.prueba"
}'


2. Eliminar un Binding

Invoke-RestMethod -Uri "http://localhost:8084/api/admin/bindings" -Method Delete -ContentType "application/json" -Body '{
  "queueName": "cola.binding.test",
  "exchangeName": "exchange.binding.test",
  "routingKey": "key.prueba"
}'
