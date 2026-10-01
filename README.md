# Sistema de Punto de Venta (POS) y Gestión de Inventario
**Almacenes San Miguel**

Un Sistema de Punto de Venta y Gestión de Inventario robusto, diseñado a medida para el control operativo y administrativo de Almacenes San Miguel.
Desarrollado bajo una arquitectura Modelo-Vista-Controlador (MVC), el sistema implementa interfaces gráficas fluidas y un mapeo objeto-relacional avanzado 
para garantizar la absoluta integridad de los datos en cada transacción.

---

## Arquitectura y Módulos Principales

El sistema está segmentado en módulos cohesivos para optimizar la operación del personal de caja, almacén y administración
reduciendo la curva de aprendizaje y mitigando errores operativos:

* **Dashboard Dinámico:**
* Tablero de resumen analítico en tiempo real que procesa las ventas del día, identifica tendencias (Top 3 de productos más vendidos)
*  y emite alertas dinámicas ante inventarios críticos.
*  
* **Punto de Venta:** Módulo transaccional con validación estricta de existencias en tiempo real, cálculo automatizado de importes
*  y generación nativa de tickets de impresión físicos.
*  
* **Gestión de Inventario:** Control integral del catálogo de artículos.
* Implementa búsquedas asíncronas, ajustes manuales de stock y la aplicación de baja lógica para preservar la inmutabilidad del historial financiero.
* 
* **Cambios y Devoluciones:** Asistente guiado para el intercambio de mercancía.
* Valida el stock disponible, consolida la diferencia de precios y ejecuta el cálculo automático de saldos a favor o en contra del cliente.
* 
* **Corte de Caja:**
*  Módulo de auditoría financiera para el control de turnos.
*  Permite el registro de fondo inicial, arqueo ciego y la conciliación de diferencias entre efectivo esperado e ingresos por tarjeta,
*   culminando en reportes impresos.

---

## Stack Tecnológico

El desarrollo se fundamenta en herramientas y estándares de la industria para asegurar escalabilidad y tolerancia a fallos:

* **Lenguaje Base:** Java
* **Interfaz Gráfica:** JavaFX (Controladores FXML y hojas de estilo CSS)
* **ORM / Persistencia:** Hibernate
* **Motor de Base de Datos:** MySQL
* **Gestión de Dependencias:** Maven
* **Control de Versiones:** Git / GitHub

---

## Instalación y Despliegue

1. **Clonación del repositorio:**
   bash
   git clone [https://github.com/angelricardo127/AlmacenesSanMiguel.git](https://github.com/angelricardo127/AlmacenesSanMiguel.git)
