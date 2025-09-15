-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: clinicaveterinaria
-- ------------------------------------------------------
-- Server version	9.0.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cita`
--

DROP TABLE IF EXISTS `cita`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cita` (
  `ID_Cita` int NOT NULL AUTO_INCREMENT,
  `Fecha_Cita` date NOT NULL,
  `Hora_Cita` time NOT NULL,
  `Duracion_Minutos` int DEFAULT '30',
  `Motivo` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Estado_Cita` enum('Programada','En_Curso','Completada','Cancelada','No_Asistio') COLLATE utf8mb4_unicode_ci DEFAULT 'Programada',
  `Observaciones` text COLLATE utf8mb4_unicode_ci,
  `Fecha_Registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ID_Mascota` int NOT NULL,
  `ID_Veterinario` int NOT NULL,
  PRIMARY KEY (`ID_Cita`),
  UNIQUE KEY `unique_vet_datetime` (`ID_Veterinario`,`Fecha_Cita`,`Hora_Cita`),
  KEY `ID_Mascota` (`ID_Mascota`),
  KEY `idx_cita_fecha` (`Fecha_Cita`),
  KEY `idx_cita_veterinario` (`ID_Veterinario`),
  CONSTRAINT `cita_ibfk_1` FOREIGN KEY (`ID_Mascota`) REFERENCES `mascota` (`ID_Mascota`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `cita_ibfk_2` FOREIGN KEY (`ID_Veterinario`) REFERENCES `veterinario` (`ID_Veterinario`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_duracion` CHECK (((`Duracion_Minutos` > 0) and (`Duracion_Minutos` <= 480)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cita`
--

LOCK TABLES `cita` WRITE;
/*!40000 ALTER TABLE `cita` DISABLE KEYS */;
/*!40000 ALTER TABLE `cita` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `ID_Cliente` int NOT NULL AUTO_INCREMENT,
  `Nombre` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Apellidos` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Identificacion` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Direccion` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Telefono` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Correo` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Fecha_Registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `Estado` enum('Activo','Inactivo') COLLATE utf8mb4_unicode_ci DEFAULT 'Activo',
  PRIMARY KEY (`ID_Cliente`),
  UNIQUE KEY `Identificacion` (`Identificacion`),
  UNIQUE KEY `Correo` (`Correo`),
  KEY `idx_cliente_identificacion` (`Identificacion`),
  KEY `idx_cliente_nombre` (`Nombre`,`Apellidos`),
  CONSTRAINT `chk_correo` CHECK ((`Correo` like _utf8mb4'%@%.%')),
  CONSTRAINT `chk_telefono` CHECK ((length(`Telefono`) >= 7))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente`
--

LOCK TABLES `cliente` WRITE;
/*!40000 ALTER TABLE `cliente` DISABLE KEYS */;
INSERT INTO `cliente` VALUES (1,'Ana María','García Silva','1020304050','Calle 123 #45-67','3101234567','ana.garcia@email.com','2025-06-22 00:46:17','Activo'),(2,'Carlos Eduardo','Martínez Ruiz','2030405060','Carrera 45 #12-34','3207654321','carlos.martinez@email.com','2025-06-22 00:46:17','Activo');
/*!40000 ALTER TABLE `cliente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entradahistoria`
--

DROP TABLE IF EXISTS `entradahistoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `entradahistoria` (
  `ID_Entrada` int NOT NULL AUTO_INCREMENT,
  `Fecha_Entrada` date NOT NULL,
  `Descripcion` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `Observaciones` text COLLATE utf8mb4_unicode_ci,
  `Peso_Actual` decimal(5,2) DEFAULT NULL,
  `Temperatura` decimal(4,2) DEFAULT NULL,
  `Frecuencia_Cardiaca` int DEFAULT NULL,
  `Fecha_Registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ID_Historia` int NOT NULL,
  `ID_Tratamiento` int DEFAULT NULL,
  PRIMARY KEY (`ID_Entrada`),
  KEY `ID_Historia` (`ID_Historia`),
  KEY `ID_Tratamiento` (`ID_Tratamiento`),
  CONSTRAINT `entradahistoria_ibfk_1` FOREIGN KEY (`ID_Historia`) REFERENCES `historiaclinica` (`ID_Historia`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `entradahistoria_ibfk_2` FOREIGN KEY (`ID_Tratamiento`) REFERENCES `tratamiento` (`ID_Tratamiento`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chk_frecuencia_cardiaca` CHECK (((`Frecuencia_Cardiaca` is null) or ((`Frecuencia_Cardiaca` >= 60) and (`Frecuencia_Cardiaca` <= 300)))),
  CONSTRAINT `chk_peso_actual` CHECK (((`Peso_Actual` is null) or (`Peso_Actual` > 0))),
  CONSTRAINT `chk_temperatura` CHECK (((`Temperatura` is null) or ((`Temperatura` >= 35.0) and (`Temperatura` <= 45.0))))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entradahistoria`
--

LOCK TABLES `entradahistoria` WRITE;
/*!40000 ALTER TABLE `entradahistoria` DISABLE KEYS */;
/*!40000 ALTER TABLE `entradahistoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `factura`
--

DROP TABLE IF EXISTS `factura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `factura` (
  `ID_Factura` int NOT NULL AUTO_INCREMENT,
  `Numero_Factura` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Fecha_Factura` date NOT NULL,
  `Subtotal` decimal(10,2) NOT NULL,
  `IVA` decimal(10,2) NOT NULL DEFAULT '0.00',
  `Descuento` decimal(10,2) DEFAULT '0.00',
  `Total` decimal(10,2) NOT NULL,
  `Estado_Factura` enum('Pendiente','Pagada','Cancelada','Vencida') COLLATE utf8mb4_unicode_ci DEFAULT 'Pendiente',
  `Fecha_Vencimiento` date DEFAULT NULL,
  `Notas` text COLLATE utf8mb4_unicode_ci,
  `Fecha_Registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ID_Cliente` int NOT NULL,
  `ID_Tratamiento` int NOT NULL,
  PRIMARY KEY (`ID_Factura`),
  UNIQUE KEY `Numero_Factura` (`Numero_Factura`),
  KEY `ID_Cliente` (`ID_Cliente`),
  KEY `ID_Tratamiento` (`ID_Tratamiento`),
  KEY `idx_factura_fecha` (`Fecha_Factura`),
  KEY `idx_factura_estado` (`Estado_Factura`),
  CONSTRAINT `factura_ibfk_1` FOREIGN KEY (`ID_Cliente`) REFERENCES `cliente` (`ID_Cliente`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `factura_ibfk_2` FOREIGN KEY (`ID_Tratamiento`) REFERENCES `tratamiento` (`ID_Tratamiento`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_descuento` CHECK ((`Descuento` >= 0)),
  CONSTRAINT `chk_fecha_vencimiento` CHECK (((`Fecha_Vencimiento` is null) or (`Fecha_Vencimiento` >= `Fecha_Factura`))),
  CONSTRAINT `chk_iva` CHECK ((`IVA` >= 0)),
  CONSTRAINT `chk_subtotal` CHECK ((`Subtotal` >= 0)),
  CONSTRAINT `chk_total` CHECK ((`Total` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `factura`
--

LOCK TABLES `factura` WRITE;
/*!40000 ALTER TABLE `factura` DISABLE KEYS */;
/*!40000 ALTER TABLE `factura` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `historiaclinica`
--

DROP TABLE IF EXISTS `historiaclinica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historiaclinica` (
  `ID_Historia` int NOT NULL AUTO_INCREMENT,
  `Fecha_Creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ID_Mascota` int NOT NULL,
  PRIMARY KEY (`ID_Historia`),
  UNIQUE KEY `ID_Mascota` (`ID_Mascota`),
  CONSTRAINT `historiaclinica_ibfk_1` FOREIGN KEY (`ID_Mascota`) REFERENCES `mascota` (`ID_Mascota`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historiaclinica`
--

LOCK TABLES `historiaclinica` WRITE;
/*!40000 ALTER TABLE `historiaclinica` DISABLE KEYS */;
/*!40000 ALTER TABLE `historiaclinica` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mascota`
--

DROP TABLE IF EXISTS `mascota`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mascota` (
  `ID_Mascota` int NOT NULL AUTO_INCREMENT,
  `Nombre` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Especie` enum('Perro','Gato','Ave','Conejo','Hamster','Reptil','Otro') COLLATE utf8mb4_unicode_ci NOT NULL,
  `Raza` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Edad` int DEFAULT NULL,
  `Peso` decimal(5,2) DEFAULT NULL,
  `Color` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Sexo` enum('Macho','Hembra') COLLATE utf8mb4_unicode_ci NOT NULL,
  `Fecha_Registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `Estado` enum('Activo','Inactivo','Fallecido') COLLATE utf8mb4_unicode_ci DEFAULT 'Activo',
  `ID_Cliente` int NOT NULL,
  PRIMARY KEY (`ID_Mascota`),
  KEY `idx_mascota_nombre` (`Nombre`),
  KEY `idx_mascota_cliente` (`ID_Cliente`),
  CONSTRAINT `mascota_ibfk_1` FOREIGN KEY (`ID_Cliente`) REFERENCES `cliente` (`ID_Cliente`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_edad` CHECK (((`Edad` >= 0) and (`Edad` <= 50))),
  CONSTRAINT `chk_peso` CHECK ((`Peso` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mascota`
--

LOCK TABLES `mascota` WRITE;
/*!40000 ALTER TABLE `mascota` DISABLE KEYS */;
/*!40000 ALTER TABLE `mascota` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pago`
--

DROP TABLE IF EXISTS `pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pago` (
  `ID_Pago` int NOT NULL AUTO_INCREMENT,
  `Numero_Pago` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Fecha_Pago` date NOT NULL,
  `Monto` decimal(10,2) NOT NULL,
  `Metodo_Pago` enum('Efectivo','Tarjeta_Credito','Tarjeta_Debito','Transferencia','Cheque') COLLATE utf8mb4_unicode_ci NOT NULL,
  `Referencia_Pago` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Notas` text COLLATE utf8mb4_unicode_ci,
  `Estado_Pago` enum('Procesado','Pendiente','Rechazado') COLLATE utf8mb4_unicode_ci DEFAULT 'Procesado',
  `Fecha_Registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ID_Factura` int NOT NULL,
  PRIMARY KEY (`ID_Pago`),
  UNIQUE KEY `Numero_Pago` (`Numero_Pago`),
  KEY `ID_Factura` (`ID_Factura`),
  CONSTRAINT `pago_ibfk_1` FOREIGN KEY (`ID_Factura`) REFERENCES `factura` (`ID_Factura`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_monto` CHECK ((`Monto` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pago`
--

LOCK TABLES `pago` WRITE;
/*!40000 ALTER TABLE `pago` DISABLE KEYS */;
/*!40000 ALTER TABLE `pago` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `ID_Producto` int NOT NULL AUTO_INCREMENT,
  `Nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Descripcion` text COLLATE utf8mb4_unicode_ci,
  `Categoria` enum('Medicamento','Vacuna','Suplemento','Material_Medico','Alimento') COLLATE utf8mb4_unicode_ci NOT NULL,
  `Cantidad_Stock` int NOT NULL DEFAULT '0',
  `Stock_Minimo` int NOT NULL DEFAULT '5',
  `Precio_Compra` decimal(10,2) NOT NULL,
  `Precio_Venta` decimal(10,2) NOT NULL,
  `Fecha_Vencimiento` date DEFAULT NULL,
  `Proveedor` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Estado` enum('Activo','Inactivo','Vencido') COLLATE utf8mb4_unicode_ci DEFAULT 'Activo',
  `Fecha_Registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID_Producto`),
  KEY `idx_producto_nombre` (`Nombre`),
  CONSTRAINT `chk_cantidad_stock` CHECK ((`Cantidad_Stock` >= 0)),
  CONSTRAINT `chk_precio_compra` CHECK ((`Precio_Compra` > 0)),
  CONSTRAINT `chk_precio_venta` CHECK ((`Precio_Venta` > 0)),
  CONSTRAINT `chk_precios` CHECK ((`Precio_Venta` >= `Precio_Compra`)),
  CONSTRAINT `chk_stock_minimo` CHECK ((`Stock_Minimo` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tratamiento`
--

DROP TABLE IF EXISTS `tratamiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tratamiento` (
  `ID_Tratamiento` int NOT NULL AUTO_INCREMENT,
  `Fecha_Tratamiento` date NOT NULL,
  `Diagnostico` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `Tratamiento_Aplicado` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `Notas` text COLLATE utf8mb4_unicode_ci,
  `Estado_Tratamiento` enum('En_Curso','Completado','Suspendido') COLLATE utf8mb4_unicode_ci DEFAULT 'En_Curso',
  `Fecha_Registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ID_Cita` int NOT NULL,
  `ID_Veterinario` int NOT NULL,
  PRIMARY KEY (`ID_Tratamiento`),
  KEY `ID_Cita` (`ID_Cita`),
  KEY `ID_Veterinario` (`ID_Veterinario`),
  CONSTRAINT `tratamiento_ibfk_1` FOREIGN KEY (`ID_Cita`) REFERENCES `cita` (`ID_Cita`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `tratamiento_ibfk_2` FOREIGN KEY (`ID_Veterinario`) REFERENCES `veterinario` (`ID_Veterinario`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tratamiento`
--

LOCK TABLES `tratamiento` WRITE;
/*!40000 ALTER TABLE `tratamiento` DISABLE KEYS */;
/*!40000 ALTER TABLE `tratamiento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tratamiento_producto`
--

DROP TABLE IF EXISTS `tratamiento_producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tratamiento_producto` (
  `ID_Tratamiento_Producto` int NOT NULL AUTO_INCREMENT,
  `ID_Tratamiento` int NOT NULL,
  `ID_Producto` int NOT NULL,
  `Cantidad_Utilizada` int NOT NULL DEFAULT '1',
  `Dosis` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Frecuencia` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Duracion_Dias` int DEFAULT NULL,
  `Fecha_Registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID_Tratamiento_Producto`),
  UNIQUE KEY `unique_tratamiento_producto` (`ID_Tratamiento`,`ID_Producto`),
  KEY `ID_Producto` (`ID_Producto`),
  CONSTRAINT `tratamiento_producto_ibfk_1` FOREIGN KEY (`ID_Tratamiento`) REFERENCES `tratamiento` (`ID_Tratamiento`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `tratamiento_producto_ibfk_2` FOREIGN KEY (`ID_Producto`) REFERENCES `producto` (`ID_Producto`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_cantidad_utilizada` CHECK ((`Cantidad_Utilizada` > 0)),
  CONSTRAINT `chk_duracion_dias` CHECK (((`Duracion_Dias` is null) or (`Duracion_Dias` > 0)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tratamiento_producto`
--

LOCK TABLES `tratamiento_producto` WRITE;
/*!40000 ALTER TABLE `tratamiento_producto` DISABLE KEYS */;
/*!40000 ALTER TABLE `tratamiento_producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `veterinario`
--

DROP TABLE IF EXISTS `veterinario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `veterinario` (
  `ID_Veterinario` int NOT NULL AUTO_INCREMENT,
  `Nombre` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Apellidos` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Identificacion` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Titulacion` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Fecha_Titulacion` date NOT NULL,
  `Telefono` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Correo` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Especialidad` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Estado` enum('Activo','Inactivo') COLLATE utf8mb4_unicode_ci DEFAULT 'Activo',
  `Fecha_Registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID_Veterinario`),
  UNIQUE KEY `Identificacion` (`Identificacion`),
  UNIQUE KEY `Correo` (`Correo`),
  CONSTRAINT `chk_vet_correo` CHECK ((`Correo` like _utf8mb4'%@%.%')),
  CONSTRAINT `chk_vet_telefono` CHECK ((length(`Telefono`) >= 7))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `veterinario`
--

LOCK TABLES `veterinario` WRITE;
/*!40000 ALTER TABLE `veterinario` DISABLE KEYS */;
INSERT INTO `veterinario` VALUES (1,'Juan Carlos','Pérez González','12345678','Médico Veterinario','2020-12-15','3001234567','juan.perez@veterinaria.com','Medicina General','Activo','2025-06-22 00:46:17'),(2,'María Elena','Rodríguez López','87654321','Médico Veterinario Zootecnista','2019-06-20','3007654321','maria.rodriguez@veterinaria.com','Cirugía','Activo','2025-06-22 00:46:17');
/*!40000 ALTER TABLE `veterinario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-21 19:56:27
