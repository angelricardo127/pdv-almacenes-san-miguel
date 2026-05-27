-- ==============================================================================
-- BASE DE DATOS: Almacenes San Miguel
-- FASE 2: DDL (Data Definition Language) - Entorno inicial para Pruebas Unitarias
-- ==============================================================================

--  Asegurar el entrono
-- Borramos la base de datos si ya existe para evitar conflictos al re-ejecutar
DROP DATABASE IF EXISTS almacenes_san_miguel;
CREATE DATABASE almacenes_san_miguel;
USE almacenes_san_miguel;


-- ==============================================================================
-- : Entidades fuertes, no dependen de ninguna otra tabla
-- ==============================================================================

-- catalogo de tallas para los uniformes
CREATE TABLE tallas (
    id_talla INT AUTO_INCREMENT PRIMARY KEY,
    talla VARCHAR(20) NOT NULL
);

-- catalogo de generos para los uniformes
CREATE TABLE generos (
    id_genero INT AUTO_INCREMENT PRIMARY KEY,
    genero VARCHAR(20) NOT NULL
);

-- catalogo de metodos de pago permitidos en caja
CREATE TABLE metodo_pago(
    id_metodo_pago INT AUTO_INCREMENT PRIMARY KEY,
    nombre_metodo VARCHAR(20) NOT NULL
);

-- catalogo de roles para el control de acceso al sistema
CREATE TABLE rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL
);

-- tabla cliente 
CREATE TABLE cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido_paterno VARCHAR(50) NOT NULL,
    apellido_materno VARCHAR(50), 
    telefono VARCHAR(20),
    correo VARCHAR(100)
);


-- ==============================================================================
--  Entidades principales, dependen de los catalogos de arriba
-- ==============================================================================

-- tabla usuario que depende de rol
CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50) UNIQUE NOT NULL, 
    contrasena VARCHAR(255) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    apellido_paterno VARCHAR(50) NOT NULL,
    id_rol INT NOT NULL,
    estatus TINYINT DEFAULT 1,
    
    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);

-- tabla productos depende de tallas y generos
CREATE TABLE productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    nombre_product VARCHAR(100) NOT NULL,
    stock INT NOT NULL,
    precio DECIMAL(10,2) NOT NULL, 
    id_talla INT NOT NULL,
    id_genero INT NOT NULL,
    
    CONSTRAINT fk_productos_tallas 
        FOREIGN KEY (id_talla) REFERENCES tallas(id_talla),
    CONSTRAINT fk_productos_generos 
        FOREIGN KEY (id_genero) REFERENCES generos(id_genero)
);


-- ==============================================================================
-- entidades transacionales dependende de los dos niveles de arriba
-- ==============================================================================

-- tabla venta es la cabecera del ticket, depende de metodo_pago
CREATE TABLE venta(
    id_venta INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    id_metodo_pago INT NOT NULL,
    
    CONSTRAINT fk_ventas_metodo_pago
        FOREIGN KEY(id_metodo_pago) REFERENCES metodo_pago(id_metodo_pago)
);

-- tabla corte de caja, depende de usuario
CREATE TABLE corte_caja (
    id_corte INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    fecha_turno DATETIME DEFAULT CURRENT_TIMESTAMP,
    fondo_inicial DECIMAL(10,2) NOT NULL,
    retiros_manuales DECIMAL(10,2) DEFAULT 0.00,
    monto_fisico DECIMAL(10,2) NOT NULL,
    
    CONSTRAINT fk_corte_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);


-- ==============================================================================
-- tablas puente y movimientos, dependen de las transacciones
-- ==============================================================================

-- tabla detalle de venta es el cuerpo del ticket, depende de venta y productos
CREATE TABLE detalle_de_venta (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL, 
    
    CONSTRAINT fk_detalle_venta
        FOREIGN KEY (id_venta) REFERENCES venta(id_venta),
    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

-- tabla devolución tiene el historial de retornos, depende de venta y productos
CREATE TABLE devolucion (
    id_devolucion INT AUTO_INCREMENT PRIMARY KEY,
    id_ventaOriginal INT NOT NULL,
    id_producto INT NOT NULL,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    motivo VARCHAR(255) NOT NULL,
    monto_retornado DECIMAL(10,2) NOT NULL,
    
    CONSTRAINT fk_devolucion_venta
        FOREIGN KEY (id_ventaOriginal) REFERENCES venta(id_venta),
    CONSTRAINT fk_devolucion_producto
        FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

ALTER TABLE productos DROP COLUMN stock;
USE almacenes_san_miguel;

-- Inyectar catalogos basicos de prueba
INSERT INTO rol (nombre_rol) VALUES ('ADMINISTRADOR'), ('CAJERO');
INSERT INTO tallas (talla) VALUES ('CHICA'), ('MEDIANA'), ('GRANDE');
INSERT INTO generos (genero) VALUES ('MASCULINO'), ('FEMENINO'), ('UNISEX');
INSERT INTO metodo_pago (nombre_metodo) VALUES ('EFECTIVO'), ('TARJETA');

-- inyectar un usuario para la prueba
INSERT INTO usuario (usuario, contrasena, nombre, apellido_paterno, id_rol) 
VALUES ('admin', 'admin123', 'Jefferson', 'Gutierritos', 1);

-- Inyectar un producto para la prueba
INSERT INTO productos (nombre_product, stock, precio, id_talla, id_genero) 
VALUES ('Playera Tipo Polo Diaria', 50, 250.00, 2, 3);

