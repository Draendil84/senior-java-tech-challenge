# 🧪 API de Gestión de Productos con Precios Históricos

Implementación completa de API REST con **arquitectura hexagonal**, **Spring Boot 3.3.0** y **H2 in-memory**.

> **📄 Especificación Completa**: Ver [`API_SPEC.yaml`](./API_SPEC.yaml) (OpenAPI 3.0) para todos los detalles.

---

## 🚀 Inicio Rápido

```bash
# Compilar y ejecutar tests
./gradlew build

# Ejecutar la aplicación
./gradlew bootRun
```

La API estará disponible en `http://localhost:8080`

### Ejemplo rápido

```bash
# 1. Crear producto
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Zapatillas", "description": "Edición 2025"}'

# 2. Agregar precio
curl -X POST http://localhost:8080/products/1/prices \
  -H "Content-Type: application/json" \
  -d '{"value": 99.99, "initDate": "2024-01-01", "endDate": "2024-06-30"}'

# 3. Obtener historial completo
curl http://localhost:8080/products/1/prices

# 4. Obtener precio en fecha específica
curl "http://localhost:8080/products/1/prices?date=2024-04-15"
```

---

## 🛠 Stack Técnico

| Componente | Versión | Razón |
|-----------|---------|-------|
| **Spring Boot** | 3.3.0 | Autoconfiguración, arranque rápido |
| **Java** | 21 | Características modernas |
| **H2 Database** | In-Memory | Desarrollo/testing sin overhead |
| **JDBC** | - | Queries SQL optimizadas |
| **JUnit 5 + Mockito** | - | Testing |

---

## 🏗 Arquitectura Hexagonal

```
REST Controller (Presentation)
    ↓
Use Cases (Application)
    ↓
Domain Logic (Product, Price, PriceRange)
    ↓
Repository Port (Interface)
    ↓
Repository Adapter (Implementation)
    ↓
H2 Database
```

**Clave**: Los use cases dependen de **puertos (interfaces)**, no de adaptadores. Esto permite:
- ✅ Bajo acoplamiento
- ✅ Testabilidad
- ✅ Cambios de tecnología sin afectar la lógica

### Estructura de Directorios

```
src/main/java/com/mango/products/
├── domain/                    # Lógica de negocio pura
│   ├── model/                 # Product, Price, PriceRange
│   ├── ports/                 # ProductRepository (interfaz)
│   └── exceptions/            # Excepciones de dominio
├── application/               # Casos de uso
│   └── usecases/              # CreateProduct, AddPrice, GetPrice
└── infrastructure/            # Detalles técnicos
    ├── persistence/           # JDBC, Mappers Domain ↔ Entity
    ├── config/                # Cache, DatabaseInitialization, Exception Handlers
    └── web/                   # REST Controllers
```

---

## 📘 Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| **POST** | `/products` | Crear producto |
| **POST** | `/products/{id}/prices` | Agregar precio a producto |
| **GET** | `/products/{id}/prices` | Historial de precios (sin `?date`) o precio en fecha (con `?date=YYYY-MM-DD`) |

### Detalles

#### POST /products
```json
"Request": {"name": "Zapatillas", "description": "..."}
"Response": {"id": 1, "name": "Zapatillas", "description": "..."}
```

**Validaciones**: `name` requerido y único, `description` máximo 1000 caracteres.

#### POST /products/{id}/prices
```json
"Request":  {"value": 99.99, "initDate": "2024-01-01", "endDate": "2024-06-30"}
"Response": {"id": 1, "name": "...", "prices": [...]}
```

**Validaciones**: `value` > 0, `initDate` requerido, `endDate` opcional, sin solapamientos de fechas.

#### GET /products/{id}/prices
```
Sin ?date    → Historial completo de precios
Con ?date    → Precio vigente en esa fecha
```

---

## 🏛️ Decisiones de Diseño

### 1. Value Object PriceRange
Encapsula toda la lógica temporal:
- Validación de rangos (initDate < endDate)
- Detección de solapamientos
- Soporte para rangos abiertos (endDate = null)

**Beneficio**: Lógica centralizada, reutilizable, testeable.

### 2. Nombres Únicos
- Constraint `UNIQUE` en BD
- Validación preventiva en `CreateProductUseCase`
- Captura de `DataIntegrityViolationException`

### 3. Endpoint Dual GET /products/{id}/prices
Se consolidó en un único endpoint por compatibilidad con scripts existentes:
```
Sin ?date    → Historial completo
Con ?date    → Precio en fecha específica
```

Nota: Idealmente serían dos endpoints separados por claridad semántica.

### 4. Rendimiento
- **Caché**: `@Cacheable` en `findById()` y `findByName()`
- **H2 In-Memory**: Acceso ultra-rápido sin I/O
- **Lazy Loading**: Precios cargados bajo demanda

---

## 🧪 Tests

```bash
./gradlew test
```

**Cobertura**:
- ✅ Tests unitarios del dominio
- ✅ Tests de casos de uso
- ✅ Tests de integración (API + persistencia)
- ✅ 15+ tests con cobertura de líneas y ramas

### Ejemplos
- `ProductTest.java`: Lógica de Product
- `CreateProductUseCaseTest.java`: Validación de inputs
- `ProductControllerIT.java`: Tests e2e de API
- `ProductRepositoryAdapterTest.java`: Tests de persistencia

---

## ✅ Cumple

- ✅ **Arquitectura Hexagonal**: Capas bien definidas
- ✅ **DDD**: Entidades, Value Objects, Excepciones de dominio
- ✅ **Validaciones Robustas**: En múltiples niveles
- ✅ **Tests**: 15+ tests unitarios e integración
- ✅ **Rendimiento**: Caché, lazy loading, H2 in-memory
- ✅ **SOLID**: Single Responsibility, Dependency Inversion, etc.
- ✅ **Documentación**: API_SPEC.yaml + código autodocumentado

---

## 📚 Referencias

- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design](https://domainlanguage.com/ddd/)
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [OpenAPI 3.0 Spec](https://spec.openapis.org/oas/v3.0.3)

