-- ============================================================
-- BASE DE DATOS: db_clientes
-- ============================================================
\c db_clientes;

CREATE TABLE IF NOT EXISTS clientes (
    id             BIGSERIAL PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    genero         VARCHAR(20),
    edad           INTEGER,
    identificacion VARCHAR(20) UNIQUE NOT NULL,
    direccion      VARCHAR(200),
    telefono       VARCHAR(20),
    cliente_id     VARCHAR(36) UNIQUE NOT NULL,
    contrasena     VARCHAR(255) NOT NULL,
    estado         BOOLEAN NOT NULL DEFAULT TRUE
);

-- Datos iniciales
INSERT INTO clientes (nombre, genero, edad, identificacion, direccion, telefono, cliente_id, contrasena, estado) VALUES
('Jose Lema',         'M', NULL, 'ID-001', 'Otavalo sn y principal', '098254785', gen_random_uuid()::text, '1234', TRUE),
('Marianela Montalvo','F', NULL, 'ID-002', 'Amazonas y NNUU',        '097548965', gen_random_uuid()::text, '5678', TRUE),
('Juan Osorio',       'M', NULL, 'ID-003', '13 junio y Equinoccial', '098874587', gen_random_uuid()::text, '1245', TRUE);

-- ============================================================
-- BASE DE DATOS: db_cuentas
-- ============================================================
\c db_cuentas;

CREATE TABLE IF NOT EXISTS clientes_cache (
    cliente_id  BIGINT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    estado      BOOLEAN NOT NULL DEFAULT TRUE,
    updated_at  TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cuentas (
    id               BIGSERIAL PRIMARY KEY,
    numero_cuenta    VARCHAR(20) UNIQUE NOT NULL,
    tipo             VARCHAR(20) NOT NULL CHECK (tipo IN ('AHORROS','CORRIENTE')),
    saldo_inicial    NUMERIC(15,2) NOT NULL DEFAULT 0,
    saldo_disponible NUMERIC(15,2) NOT NULL DEFAULT 0,
    estado           BOOLEAN NOT NULL DEFAULT TRUE,
    cliente_id       BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS movimientos (
    id               BIGSERIAL PRIMARY KEY,
    fecha            TIMESTAMP NOT NULL DEFAULT NOW(),
    tipo_movimiento  VARCHAR(20) NOT NULL CHECK (tipo_movimiento IN ('DEPOSITO','RETIRO')),
    valor            NUMERIC(15,2) NOT NULL,
    saldo            NUMERIC(15,2) NOT NULL,
    cuenta_id        BIGINT NOT NULL REFERENCES cuentas(id)
);

CREATE INDEX IF NOT EXISTS idx_movimientos_cuenta_id ON movimientos(cuenta_id);
CREATE INDEX IF NOT EXISTS idx_movimientos_fecha     ON movimientos(fecha);
CREATE INDEX IF NOT EXISTS idx_cuentas_cliente_id    ON cuentas(cliente_id);

-- Seed clientes_cache (IDs deben coincidir con db_clientes)
INSERT INTO clientes_cache (cliente_id, nombre, estado, updated_at) VALUES
(1, 'Jose Lema',          TRUE, NOW()),
(2, 'Marianela Montalvo', TRUE, NOW()),
(3, 'Juan Osorio',        TRUE, NOW());

-- Cuentas
INSERT INTO cuentas (numero_cuenta, tipo, saldo_inicial, saldo_disponible, estado, cliente_id) VALUES
('478758', 'AHORROS',   2000.00, 2000.00, TRUE, 1),
('225487', 'CORRIENTE',  100.00,  100.00, TRUE, 2),
('495878', 'AHORROS',      0.00,    0.00, TRUE, 3),
('496825', 'AHORROS',    540.00,  540.00, TRUE, 2),
('585545', 'CORRIENTE', 1000.00, 1000.00, TRUE, 1);

-- Movimientos
-- 478758: Retiro 575 → saldo 1425
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id) VALUES
(NOW(), 'RETIRO',   575.00, 1425.00, 1);
UPDATE cuentas SET saldo_disponible = 1425.00 WHERE numero_cuenta = '478758';

-- 225487: Depósito 600 → saldo 700
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id) VALUES
(NOW(), 'DEPOSITO', 600.00,  700.00, 2);
UPDATE cuentas SET saldo_disponible = 700.00 WHERE numero_cuenta = '225487';

-- 495878: Depósito 150 → saldo 150
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id) VALUES
(NOW(), 'DEPOSITO', 150.00,  150.00, 3);
UPDATE cuentas SET saldo_disponible = 150.00 WHERE numero_cuenta = '495878';

-- 496825: Retiro 540 → saldo 0
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id) VALUES
(NOW(), 'RETIRO',   540.00,    0.00, 4);
UPDATE cuentas SET saldo_disponible = 0.00 WHERE numero_cuenta = '496825';
