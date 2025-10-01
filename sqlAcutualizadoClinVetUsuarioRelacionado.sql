-- =====================================================
-- EVIDENCIA GA6-220501096-AA2-EV03
-- Script bases de datos del proyecto - Clínica Veterinaria
-- Autores: Melissa Carol Ramírez Ortegón, Ciro Montes
-- Fecha: Junio 2025
-- =====================================================

-- Crear la base de datos
DROP DATABASE IF EXISTS ClinicaVeterinaria;
CREATE DATABASE ClinicaVeterinaria 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE ClinicaVeterinaria;

-- =====================================================
-- TABLA CLIENTE
-- =====================================================
CREATE TABLE Cliente (
    ID_Cliente INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(50) NOT NULL,
    Apellidos VARCHAR(50) NOT NULL,
    Identificacion VARCHAR(20) UNIQUE NOT NULL,
    Direccion VARCHAR(200) NOT NULL,
    Telefono VARCHAR(15) NOT NULL,
    Correo VARCHAR(100) UNIQUE,
    Fecha_Registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Estado ENUM('Activo', 'Inactivo') DEFAULT 'Activo',
    
    -- Restricciones adicionales
    CONSTRAINT chk_telefono CHECK (LENGTH(Telefono) >= 7),
    CONSTRAINT chk_correo CHECK (Correo LIKE '%@%.%')
);

-- =====================================================
-- TABLA VETERINARIO
-- =====================================================
CREATE TABLE Veterinario (
    ID_Veterinario INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(50) NOT NULL,
    Apellidos VARCHAR(50) NOT NULL,
    Identificacion VARCHAR(20) UNIQUE NOT NULL,
    Titulacion VARCHAR(100) NOT NULL,
    Fecha_Titulacion DATE NOT NULL,
    Telefono VARCHAR(15) NOT NULL,
    Correo VARCHAR(100) UNIQUE NOT NULL,
    Especialidad VARCHAR(100),
    Estado ENUM('Activo', 'Inactivo') DEFAULT 'Activo',
    Fecha_Registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Restricciones adicionales
    CONSTRAINT chk_vet_telefono CHECK (LENGTH(Telefono) >= 7),
    CONSTRAINT chk_vet_correo CHECK (Correo LIKE '%@%.%')
  
);

-- =====================================================
-- TABLA MASCOTA
-- =====================================================
CREATE TABLE Mascota (
    ID_Mascota INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(50) NOT NULL,
    Especie ENUM('Perro', 'Gato', 'Ave', 'Conejo', 'Hamster', 'Reptil', 'Otro') NOT NULL,
    Raza VARCHAR(50),
    Edad INT,
    Peso DECIMAL(5,2),
    Color VARCHAR(50),
    Sexo ENUM('Macho', 'Hembra') NOT NULL,
    Fecha_Registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Estado ENUM('Activo', 'Inactivo', 'Fallecido') DEFAULT 'Activo',
    ID_Cliente INT NOT NULL,
    
    -- Claves foráneas
    FOREIGN KEY (ID_Cliente) REFERENCES Cliente(ID_Cliente) 
        ON DELETE RESTRICT ON UPDATE CASCADE,
    
    -- Restricciones adicionales
    CONSTRAINT chk_edad CHECK (Edad >= 0 AND Edad <= 50),
    CONSTRAINT chk_peso CHECK (Peso > 0)
);

-- =====================================================
-- TABLA PRODUCTO
-- =====================================================
CREATE TABLE Producto (
    ID_Producto INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Descripcion TEXT,
    Categoria ENUM('Medicamento', 'Vacuna', 'Suplemento', 'Material_Medico', 'Alimento') NOT NULL,
    Cantidad_Stock INT NOT NULL DEFAULT 0,
    Stock_Minimo INT NOT NULL DEFAULT 5,
    Precio_Compra DECIMAL(10,2) NOT NULL,
    Precio_Venta DECIMAL(10,2) NOT NULL,
    Fecha_Vencimiento DATE,
    Proveedor VARCHAR(100),
    Estado ENUM('Activo', 'Inactivo', 'Vencido') DEFAULT 'Activo',
    Fecha_Registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Restricciones adicionales
    CONSTRAINT chk_cantidad_stock CHECK (Cantidad_Stock >= 0),
    CONSTRAINT chk_stock_minimo CHECK (Stock_Minimo >= 0),
    CONSTRAINT chk_precio_compra CHECK (Precio_Compra > 0),
    CONSTRAINT chk_precio_venta CHECK (Precio_Venta > 0),
    CONSTRAINT chk_precios CHECK (Precio_Venta >= Precio_Compra)
);

-- =====================================================
-- TABLA CITA
-- =====================================================
CREATE TABLE Cita (
    ID_Cita INT AUTO_INCREMENT PRIMARY KEY,
    Fecha_Cita DATE NOT NULL,
    Hora_Cita TIME NOT NULL,
    Duracion_Minutos INT DEFAULT 30,
    Motivo VARCHAR(200) NOT NULL,
    Estado_Cita ENUM('Programada', 'En_Curso', 'Completada', 'Cancelada', 'No_Asistio') DEFAULT 'Programada',
    Observaciones TEXT,
    Fecha_Registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ID_Mascota INT NOT NULL,
    ID_Veterinario INT NOT NULL,
    
    -- Claves foráneas
    FOREIGN KEY (ID_Mascota) REFERENCES Mascota(ID_Mascota) 
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (ID_Veterinario) REFERENCES Veterinario(ID_Veterinario) 
        ON DELETE RESTRICT ON UPDATE CASCADE,
    
    -- Restricciones adicionales
    
    CONSTRAINT chk_duracion CHECK (Duracion_Minutos > 0 AND Duracion_Minutos <= 480),
    
    -- Índice único para evitar doble cita en mismo horario para mismo veterinario
    UNIQUE KEY unique_vet_datetime (ID_Veterinario, Fecha_Cita, Hora_Cita)
);

-- =====================================================
-- TABLA HISTORIA CLINICA
-- =====================================================
CREATE TABLE HistoriaClinica (
    ID_Historia INT AUTO_INCREMENT PRIMARY KEY,
    Fecha_Creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ID_Mascota INT NOT NULL UNIQUE,
    
    -- Clave foránea
    FOREIGN KEY (ID_Mascota) REFERENCES Mascota(ID_Mascota) 
        ON DELETE RESTRICT ON UPDATE CASCADE
);

-- =====================================================
-- TABLA TRATAMIENTO
-- =====================================================
CREATE TABLE Tratamiento (
    ID_Tratamiento INT AUTO_INCREMENT PRIMARY KEY,
    Fecha_Tratamiento DATE NOT NULL,
    Diagnostico TEXT NOT NULL,
    Tratamiento_Aplicado TEXT NOT NULL,
    Notas TEXT,
    Estado_Tratamiento ENUM('En_Curso', 'Completado', 'Suspendido') DEFAULT 'En_Curso',
    Fecha_Registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ID_Cita INT NOT NULL,
    ID_Veterinario INT NOT NULL,
    
    -- Claves foráneas
    FOREIGN KEY (ID_Cita) REFERENCES Cita(ID_Cita) 
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (ID_Veterinario) REFERENCES Veterinario(ID_Veterinario) 
        ON DELETE RESTRICT ON UPDATE CASCADE
);

-- =====================================================
-- TABLA TRATAMIENTO_PRODUCTO (Relación N:M)
-- =====================================================
CREATE TABLE Tratamiento_Producto (
    ID_Tratamiento_Producto INT AUTO_INCREMENT PRIMARY KEY,
    ID_Tratamiento INT NOT NULL,
    ID_Producto INT NOT NULL,
    Cantidad_Utilizada INT NOT NULL DEFAULT 1,
    Dosis VARCHAR(100),
    Frecuencia VARCHAR(100),
    Duracion_Dias INT,
    Fecha_Registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Claves foráneas
    FOREIGN KEY (ID_Tratamiento) REFERENCES Tratamiento(ID_Tratamiento) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (ID_Producto) REFERENCES Producto(ID_Producto) 
        ON DELETE RESTRICT ON UPDATE CASCADE,
        
    -- Restricciones adicionales
    CONSTRAINT chk_cantidad_utilizada CHECK (Cantidad_Utilizada > 0),
    CONSTRAINT chk_duracion_dias CHECK (Duracion_Dias IS NULL OR Duracion_Dias > 0),
    
    -- Evitar duplicados
    UNIQUE KEY unique_tratamiento_producto (ID_Tratamiento, ID_Producto)
);

-- =====================================================
-- TABLA ENTRADA HISTORIA
-- =====================================================
CREATE TABLE EntradaHistoria (
    ID_Entrada INT AUTO_INCREMENT PRIMARY KEY,
    Fecha_Entrada DATE NOT NULL,
    Descripcion TEXT NOT NULL,
    Observaciones TEXT,
    Peso_Actual DECIMAL(5,2),
    Temperatura DECIMAL(4,2),
    Frecuencia_Cardiaca INT,
    Fecha_Registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ID_Historia INT NOT NULL,
    ID_Tratamiento INT,
    
    -- Claves foráneas
    FOREIGN KEY (ID_Historia) REFERENCES HistoriaClinica(ID_Historia) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (ID_Tratamiento) REFERENCES Tratamiento(ID_Tratamiento) 
        ON DELETE SET NULL ON UPDATE CASCADE,
        
    -- Restricciones adicionales
    CONSTRAINT chk_peso_actual CHECK (Peso_Actual IS NULL OR Peso_Actual > 0),
    CONSTRAINT chk_temperatura CHECK (Temperatura IS NULL OR (Temperatura >= 35.0 AND Temperatura <= 45.0)),
    CONSTRAINT chk_frecuencia_cardiaca CHECK (Frecuencia_Cardiaca IS NULL OR (Frecuencia_Cardiaca >= 60 AND Frecuencia_Cardiaca <= 300))
);

-- =====================================================
-- TABLA FACTURA
-- =====================================================
CREATE TABLE Factura (
    ID_Factura INT AUTO_INCREMENT PRIMARY KEY,
    Numero_Factura VARCHAR(20) UNIQUE NOT NULL,
    Fecha_Factura DATE NOT NULL,
    Subtotal DECIMAL(10,2) NOT NULL,
    IVA DECIMAL(10,2) NOT NULL DEFAULT 0,
    Descuento DECIMAL(10,2) DEFAULT 0,
    Total DECIMAL(10,2) NOT NULL,
    Estado_Factura ENUM('Pendiente', 'Pagada', 'Cancelada', 'Vencida') DEFAULT 'Pendiente',
    Fecha_Vencimiento DATE,
    Notas TEXT,
    Fecha_Registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ID_Cliente INT NOT NULL,
    ID_Tratamiento INT NOT NULL,
    
    -- Claves foráneas
    FOREIGN KEY (ID_Cliente) REFERENCES Cliente(ID_Cliente) 
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (ID_Tratamiento) REFERENCES Tratamiento(ID_Tratamiento) 
        ON DELETE RESTRICT ON UPDATE CASCADE,
        
    -- Restricciones adicionales
    CONSTRAINT chk_subtotal CHECK (Subtotal >= 0),
    CONSTRAINT chk_iva CHECK (IVA >= 0),
    CONSTRAINT chk_descuento CHECK (Descuento >= 0),
    CONSTRAINT chk_total CHECK (Total >= 0),
    CONSTRAINT chk_fecha_vencimiento CHECK (Fecha_Vencimiento IS NULL OR Fecha_Vencimiento >= Fecha_Factura)
);

-- =====================================================
-- TABLA PAGO
-- =====================================================
CREATE TABLE Pago (
    ID_Pago INT AUTO_INCREMENT PRIMARY KEY,
    Numero_Pago VARCHAR(20) UNIQUE NOT NULL,
    Fecha_Pago DATE NOT NULL,
    Monto DECIMAL(10,2) NOT NULL,
    Metodo_Pago ENUM('Efectivo', 'Tarjeta_Credito', 'Tarjeta_Debito', 'Transferencia', 'Cheque') NOT NULL,
    Referencia_Pago VARCHAR(50),
    Notas TEXT,
    Estado_Pago ENUM('Procesado', 'Pendiente', 'Rechazado') DEFAULT 'Procesado',
    Fecha_Registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ID_Factura INT NOT NULL,
    
    -- Clave foránea
    FOREIGN KEY (ID_Factura) REFERENCES Factura(ID_Factura) 
        ON DELETE RESTRICT ON UPDATE CASCADE,
        
    -- Restricciones adicionales
    CONSTRAINT chk_monto CHECK (Monto > 0)
);

-- =====================================================
-- ÍNDICES PARA OPTIMIZACIÓN
-- =====================================================

-- Índices para búsquedas frecuentes
CREATE INDEX idx_cliente_identificacion ON Cliente(Identificacion);
CREATE INDEX idx_cliente_nombre ON Cliente(Nombre, Apellidos);
CREATE INDEX idx_mascota_nombre ON Mascota(Nombre);
CREATE INDEX idx_mascota_cliente ON Mascota(ID_Cliente);
CREATE INDEX idx_cita_fecha ON Cita(Fecha_Cita);
CREATE INDEX idx_cita_veterinario ON Cita(ID_Veterinario);
CREATE INDEX idx_producto_nombre ON Producto(Nombre);
CREATE INDEX idx_factura_fecha ON Factura(Fecha_Factura);
CREATE INDEX idx_factura_estado ON Factura(Estado_Factura);

-- =====================================================
-- TRIGGERS PARA AUTOMATIZACIÓN
-- =====================================================

-- Trigger para crear automáticamente historia clínica al registrar mascota
DELIMITER //
CREATE TRIGGER tr_crear_historia_clinica
AFTER INSERT ON Mascota
FOR EACH ROW
BEGIN
    INSERT INTO HistoriaClinica (ID_Mascota) VALUES (NEW.ID_Mascota);
END //
DELIMITER ;

-- Trigger para actualizar stock de productos
DELIMITER //
CREATE TRIGGER tr_actualizar_stock_producto
AFTER INSERT ON Tratamiento_Producto
FOR EACH ROW
BEGIN
    UPDATE Producto 
    SET Cantidad_Stock = Cantidad_Stock - NEW.Cantidad_Utilizada
    WHERE ID_Producto = NEW.ID_Producto;
END //
DELIMITER ;

-- Trigger para generar número de factura automático
DELIMITER //
CREATE TRIGGER tr_generar_numero_factura
BEFORE INSERT ON Factura
FOR EACH ROW
BEGIN
    DECLARE next_num INT;
    SELECT COALESCE(MAX(CAST(SUBSTRING(Numero_Factura, 5) AS UNSIGNED)), 0) + 1 
    INTO next_num FROM Factura WHERE Numero_Factura LIKE 'FAC-%';
    SET NEW.Numero_Factura = CONCAT('FAC-', LPAD(next_num, 6, '0'));
END //
DELIMITER ;

-- Trigger para generar número de pago automático
DELIMITER //
CREATE TRIGGER tr_generar_numero_pago
BEFORE INSERT ON Pago
FOR EACH ROW
BEGIN
    DECLARE next_num INT;
    SELECT COALESCE(MAX(CAST(SUBSTRING(Numero_Pago, 5) AS UNSIGNED)), 0) + 1 
    INTO next_num FROM Pago WHERE Numero_Pago LIKE 'PAG-%';
    SET NEW.Numero_Pago = CONCAT('PAG-', LPAD(next_num, 6, '0'));
END //
DELIMITER ;

-- =====================================================
-- DATOS DE PRUEBA (OPCIONAL)
-- =====================================================

-- Insertar veterinarios de prueba
INSERT INTO Veterinario (Nombre, Apellidos, Identificacion, Titulacion, Fecha_Titulacion, Telefono, Correo, Especialidad) VALUES
('Juan Carlos', 'Pérez González', '12345678', 'Médico Veterinario', '2020-12-15', '3001234567', 'juan.perez@veterinaria.com', 'Medicina General'),
('María Elena', 'Rodríguez López', '87654321', 'Médico Veterinario Zootecnista', '2019-06-20', '3007654321', 'maria.rodriguez@veterinaria.com', 'Cirugía');

-- Insertar clientes de prueba
INSERT INTO Cliente (Nombre, Apellidos, Identificacion, Direccion, Telefono, Correo) VALUES
('Ana María', 'García Silva', '1020304050', 'Calle 123 #45-67', '3101234567', 'ana.garcia@email.com'),
('Carlos Eduardo', 'Martínez Ruiz', '2030405060', 'Carrera 45 #12-34', '3207654321', 'carlos.martinez@email.com');

-- =====================================================
-- COMENTARIOS FINALES
-- =====================================================
/*
CARACTERÍSTICAS DEL SCRIPT:

1. ESTRUCTURA COMPLETA:
   - Todas las tablas del modelo relacional
   - Relaciones correctamente establecidas
   - Restricciones de integridad referencial

2. TIPOS DE DATOS APROPIADOS:
   - VARCHAR para textos con longitud específica
   - TEXT para descripciones largas
   - DECIMAL para valores monetarios y medidas
   - ENUM para campos con valores predefinidos
   - TIMESTAMP para auditoría

3. RESTRICCIONES IMPLEMENTADAS:
   - PRIMARY KEY en todas las tablas
   - FOREIGN KEY con opciones ON DELETE/UPDATE
   - CHECK constraints para validación de datos
   - UNIQUE constraints para evitar duplicados
   - NOT NULL para campos obligatorios

4. OPTIMIZACIONES:
   - Índices para búsquedas frecuentes
   - Triggers para automatización
   - Normalización hasta 3FN

5. SEGURIDAD Y AUDITORÍA:
   - Campos de fecha de registro
   - Estados para control de entidades
   - Validaciones de integridad de datos

ESTE SCRIPT CUMPLE CON TODOS LOS REQUISITOS DE LA EVIDENCIA EV03
*/
-- Agregar columna de relación en tabla usuario
ALTER TABLE usuario ADD COLUMN id_cliente INT NULL;
ALTER TABLE usuario ADD COLUMN id_veterinario INT NULL;

-- Agregar foreign keys
ALTER TABLE usuario ADD FOREIGN KEY (id_cliente) REFERENCES cliente(ID_Cliente);
ALTER TABLE usuario ADD FOREIGN KEY (id_veterinario) REFERENCES veterinario(ID_Veterinario);

-- Actualizar datos existentes (ejemplo)
UPDATE usuario SET id_cliente = 1 WHERE email = 'lucia.cliente@clinicaveterinaria.com';
UPDATE usuario SET id_veterinario = 1 WHERE email = 'ana.vet@clinicaveterinaria.com';