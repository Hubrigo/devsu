# Devsu Banking — Microservicios Bancarios

Solución de gestión de clientes, cuentas y movimientos bancarios implementada con arquitectura de microservicios.

## Arquitectura

```
┌─────────────┐     RabbitMQ      ┌──────────────┐
│ ms-clientes │ ──────────────▶   │  ms-cuentas  │
│  port 8081  │  cliente.exchange  │  port 8082   │
│  db_clientes│                   │  db_cuentas  │
└─────────────┘                   └──────────────┘
        ▲                                ▲
        └──────────── Frontend ──────────┘
                      port 80
```

### Servicios
| Servicio | Puerto | Base de datos | Swagger |
|----------|--------|---------------|---------|
| ms-clientes | 8081 | db_clientes (PostgreSQL :5432) | http://localhost:8081/swagger-ui.html |
| ms-cuentas  | 8082 | db_cuentas (PostgreSQL :5433)  | http://localhost:8082/swagger-ui.html |
| frontend    | 80   | —                              | http://localhost |
| RabbitMQ    | 5672 | —                              | http://localhost:15672 (guest/guest) |

### Comunicación asincrónica (RabbitMQ)
- **Exchange**: `cliente.exchange` (topic)
- **Routing keys**: `cliente.created`, `cliente.updated`, `cliente.deleted`
- **Queue**: `cuentas.cliente.events`
- **Flujo**: cuando se crea/actualiza/elimina un cliente en `ms-clientes`, se publica un evento. `ms-cuentas` consume ese evento y mantiene una tabla `clientes_cache` que usa para validar clientes antes de crear cuentas.

## Ejecutar con Docker

```bash
git clone <repo-url>
cd devsu
docker-compose up --build
```

La primera ejecución tarda ~3-5 minutos (descarga de imágenes y compilación Maven).

## Ejecutar localmente

### Prerequisitos
- Java 17, Maven 3.9+, Node.js 20+
- PostgreSQL 15 en puertos 5432 y 5433
- RabbitMQ en puerto 5672

```bash
# ms-clientes
cd ms-clientes && mvn spring-boot:run

# ms-cuentas
cd ms-cuentas && mvn spring-boot:run

# frontend
cd frontend && npm install && npm run dev
```

## Correr pruebas

```bash
# Unitarias
cd ms-clientes && mvn test -Dtest="**/unit/**"
cd ms-cuentas  && mvn test -Dtest="**/unit/**"

# Integración
cd ms-clientes && mvn test -Dspring.profiles.active=test
cd ms-cuentas  && mvn test -Dspring.profiles.active=test

# Todo
cd ms-clientes && mvn verify
cd ms-cuentas  && mvn verify
```

## Endpoints

### ms-clientes (http://localhost:8081)
| Método | URL | Descripción |
|--------|-----|-------------|
| POST   | /clientes | Crear cliente |
| GET    | /clientes | Listar clientes |
| GET    | /clientes/{id} | Buscar por ID |
| PUT    | /clientes/{id} | Actualizar completo |
| PATCH  | /clientes/{id} | Actualizar parcial |
| DELETE | /clientes/{id} | Desactivar |

### ms-cuentas (http://localhost:8082)
| Método | URL | Descripción |
|--------|-----|-------------|
| POST   | /cuentas | Crear cuenta |
| GET    | /cuentas | Listar cuentas |
| GET    | /cuentas/{id} | Buscar por ID |
| PUT    | /cuentas/{id} | Actualizar completo |
| PATCH  | /cuentas/{id} | Actualizar parcial |
| POST   | /movimientos | Registrar movimiento |
| GET    | /movimientos | Listar movimientos |
| GET    | /movimientos/{id} | Buscar por ID |
| PUT    | /movimientos/{id} | Actualizar |
| GET    | /reportes?fechaInicio=YYYY-MM-DD&fechaFin=YYYY-MM-DD&clienteId=1 | Estado de cuenta |

## Validación de saldo insuficiente

```json
{
  "timestamp": "2024-02-10T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Saldo no disponible",
  "path": "/movimientos"
}
```

## Postman

Importar `postman/DevsuBanking.postman_collection.json`. Variables: `ms_clientes=http://localhost:8081`, `ms_cuentas=http://localhost:8082`.

## Saldos esperados con datos iniciales

| Cuenta | Tipo | Saldo Inicial | Movimiento | Saldo Final |
|--------|------|--------------|------------|-------------|
| 478758 | Ahorros   | 2000 | -575 Retiro   | **1425** |
| 225487 | Corriente | 100  | +600 Depósito | **700**  |
| 495878 | Ahorros   | 0    | +150 Depósito | **150**  |
| 496825 | Ahorros   | 540  | -540 Retiro   | **0**    |
| 585545 | Corriente | 1000 | —             | **1000** |
