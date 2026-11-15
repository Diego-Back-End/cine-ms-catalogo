# ms-catalogo — CineCloud

Microservicio REST del catálogo de CineCloud: películas, géneros, clasificaciones, estados, sucursales y funciones. Es la API que consumen los frontends de administración y de clientes.

- **Stack:** Spring Boot 4.1.1, Java 21, Spring Data JPA, Spring Security (OAuth2 Resource Server), MySQL, Lombok.
- **Puerto:** `8081`.
- **Prefijo de endpoints:** `/api`.

## Requisitos

- JDK 21.
- MySQL 8.x escuchando en `localhost:3306`.
- No hace falta Maven instalado: el proyecto incluye el wrapper (`./mvnw`, `mvnw.cmd` en Windows).

## Configuración

1. Crear la base de datos:

   ```sql
   CREATE DATABASE cine_catalogo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. Usar la plantilla de configuración. Si no tenés `application.yml`, copiala:

   ```bash
   cp src/main/resources/application.yml.example src/main/resources/application.yml
   ```

   En Windows:

   ```powershell
   Copy-Item src/main/resources/application.yml.example src/main/resources/application.yml
   ```

   Si el archivo ya existe (el repo trae uno con credenciales locales de desarrollo), editalo directamente.

3. Editar `src/main/resources/application.yml`:

   | Propiedad | Qué poner |
   |---|---|
   | `spring.datasource.username` / `password` | Usuario y contraseña de tu MySQL local. |
   | `spring.security.oauth2.resourceserver.jwt.issuer-uri` | `https://login.microsoftonline.com/{tenant-id}/v2.0`. El `{tenant-id}` es el mismo `VITE_AZURE_AUTHORITY` del frontend admin. |
   | `spring.security.oauth2.resourceserver.jwt.audiences` | `api://{client-id}`, el mismo `VITE_AZURE_CLIENT_ID` del frontend admin. |

   El proyecto **no lee variables de entorno**: la configuración se edita directamente en el YAML. Si vas a publicar este repositorio, no dejes credenciales reales en `application.yml` (agregalo a `.gitignore` y usá `application.yml.example` como plantilla).

4. Con la base creada, arrancar:

   ```bash
   ./mvnw spring-boot:run
   ```

   En Windows:

   ```powershell
   mvnw.cmd spring-boot:run
   ```

   La app queda en `http://localhost:8081`.

Al iniciar, Hibernate crea/actualiza las tablas (`ddl-auto: update`) y `SeedConfig` carga los géneros y clasificaciones base si no existen.

## Verificación

```bash
./mvnw test
```

El único test es de carga de contexto: **necesita MySQL levantado y el tenant de Azure accesible** (`JwtDecoders.fromIssuerLocation` se ejecuta al arrancar). Si el tenant no responde, el contexto falla.

## Endpoints

Las lecturas del catálogo son públicas (el frontend cliente no envía token). Las escrituras exigen un JWT de Azure Entra ID con el claim `roles`.

| Método | Ruta | Acceso |
|---|---|---|
| GET | `/api/peliculas` (`?search=&genero=&rating=&estado=`) | Público |
| GET | `/api/peliculas/{id}` | Público |
| GET | `/api/peliculas/slug/{slug}` | Público |
| POST | `/api/peliculas` | `super_admin` |
| PUT | `/api/peliculas/{id}` | `super_admin` |
| DELETE | `/api/peliculas/{id}` | `super_admin` |
| GET | `/api/generos` | Público |
| POST | `/api/generos` | `super_admin` |
| GET | `/api/clasificaciones` | Público |
| POST | `/api/clasificaciones` | `super_admin` |
| GET | `/api/estados` | Público |
| GET | `/api/sucursales` | Público |
| GET | `/api/sucursales/{id}` | Autenticado |
| POST | `/api/sucursales` | `super_admin` |
| PUT | `/api/sucursales/{id}` | `super_admin` |
| GET | `/api/funciones` (`?sucursalId=`) | Público |
| POST | `/api/funciones` | `branch_admin` |
| POST | `/api/funciones/limpiar-vencidas` (`?sucursalId=`) | `branch_admin` |

Cualquier ruta no listada cae en `anyRequest().authenticated()`.

### Seguridad

- La allow-list está hardcodeada en `config/SecurityConfig.java`. **Un endpoint nuevo devuelve 401 hasta que se agregue ahí.**
- Los roles llegan en el claim `roles` como `super_admin` / `branch_admin`, sin prefijo `ROLE_`.
- `POST /api/funciones` solo acepta `branch_admin` (aunque el comentario del código sugiera ambos roles).
- Los orígenes CORS están en `SecurityConfig.corsConfigurationSource()` y también en `config/CorsConfig.java`; hay que mantener ambos en sincronía al agregar un frontend nuevo (`http://localhost:5173`, `http://localhost:5174`, `https://diego-back-end.github.io`).
- `GlobalExceptionHandler` solo traduce `IllegalArgumentException` a `400 { "error": "..." }`.

## Modelo de datos

| Entidad | Campos principales |
|---|---|
| `Pelicula` | `titulo`, `slug` (único), `sinopsis`, `duracionMinutos`, `clasificacion`, `imagenUrl`, `estado`, `generos` |
| `Genero` | `nombre` (único, sin distinguir mayúsculas) |
| `Clasificacion` | `nombre` (único, sin distinguir mayúsculas) |
| `Sucursal` | `nombre`, `direccion`, `ciudad`, `cantidadSalas`, `tiposSala`, `adminEmail` |
| `Funcion` | `salaId`, `sucursalId`, `fechaHora`, `formato`, `idioma`, `pelicula` |

La lista de estados vive en `controllers/EstadoController.java`; el frontend admin la duplica en `CatalogMetaContext` y `data/peliculasMock.js`.

## Estructura

```
src/main/java/com/cine/ms_catalogo
├── config/        SecurityConfig, CorsConfig, SeedConfig, GlobalExceptionHandler, AudienceValidator
├── controllers/   Endpoints REST
├── dto/           Requests y responses
├── entities/      Entidades JPA
├── repositories/  Spring Data repositories
├── services/      Lógica de negocio
└── utils/         SlugUtils
```

## Notas

- Reservas, butacas y pagos **no** viven acá: los maneja un `ms-reservas` separado que no forma parte de este repositorio.
- El catálogo no llama a otros servicios; es autocontenido salvo por MySQL y el proveedor de identidad.
