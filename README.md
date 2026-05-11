Keycloak SSO App

  Aplicación Spring Boot con autenticación SSO via Keycloak usando OAuth2/OIDC.

Tecnologías

  - Java 21
  - Spring Boot 4.x
  - Spring Security OAuth2 Client
  - Keycloak 23.0
  - Thymeleaf + Bootstrap
  - Docker + Docker Compose
  - Swagger/OpenAPI (SpringDoc)


  Requisitos previos

  - Docker Desktop: (https://www.docker.com/products/docker-desktop/) instalado y corriendo
  - Puertos `8080` y `8180` disponibles en tu máquina



Instalación y uso

  1. Clonar el repositorio

bash
  - git clone https://github.com/Mats210705/appSpringBootSSO.git
  - cd appSpringBootSSO


2. Levantar el proyecto

  docker compose up --build


  Esto descarga las imágenes necesarias, construye la aplicación y levanta:
  - Keycloak en `http://localhost:8180` con el realm, client y usuario de prueba ya configurados
  - Aplicación Spring Boot en `http://localhost:8080`

  > La primera vez puede tardar varios minutos mientras Docker descarga las imágenes.

3. Acceder a la aplicación

  Una vez que ambos servicios estén corriendo:

  1. Abrí `http://localhost:8080`
  2. Hacé click en **"Iniciar sesión con Keycloak"**
  3. Ingresá las credenciales del usuario de prueba
  4. Serás redirigido al dashboard con tu información de usuario

4. Detener el proyecto

  docker compose down

  Usuario de prueba

| Campo    | Valor                     |
|----------|---------------------------|
| Username | `orbit-testuser`          |
| Password | `orbit1234`               |
| Email    | `orbit-testuser@test.com` |



API REST

  GET /api/me

  Devuelve la información del usuario autenticado.
  Requiere autenticación.
  Respuesta exitosa (200):

    {
      "username": "orbit-testuser",
      "email": "orbit-testuser@test.com",
      "fullName": "Orbit Testuser",
      "roles": []
    }


  Sin autenticación: redirige a la página de login (302).

  Documentación completa disponible en `http://localhost:8080/swagger-ui.html`.



Consola de administración Keycloak

| Campo    | Valor                   |
|----------|-------------------------|
| URL      | `http://localhost:8180` |
| Usuario  | `admin`                 |
| Password | `admin`                 |
| Realm    | `sso-realm`             |



Decisiones de diseño

- OAuth2 Authorization Code Flow: flujo estándar para aplicaciones web con servidor backend, más seguro que el flujo implícito.
- RP-Initiated Logout: el logout invalida la sesión tanto en la aplicación como en Keycloak, evitando que el usuario quede autenticado en el proveedor de identidad tras cerrar sesión.
- Arquitectura en capas: separación clara entre Controller, Service y DTO para mantener responsabilidades bien definidas y facilitar el testing.
- Perfiles de configuración: uso de `application.properties` + `application-dev.properties` para separar configuración base de configuración de entorno, es importante aclarar en que existe un entorno productivo, y puede crearse un entorno testing con consideraciones del QA.
- Manejo de excepciones centralizado: `GlobalExceptionHandler` con `@RestControllerAdvice` para respuestas de error consistentes en toda la API.
- Swagger/OpenAPI: documentación automática de endpoints disponible en `http://localhost:8080/swagger-ui/index.html`o en formato JSON `http://localhost:8080/api-docs`.




Estructura del proyecto


  src/
  ├── main/
  │   ├── java/com/orbit/sso/
  │   │   ├── config/
  │   │   │   ├── SecurityConfig.java
  │   │   │   └── SwaggerConfig.java
  │   │   ├── controller/
  │   │   │   ├── ApiMeController.java
  │   │   │   └── AuthController.java
  │   │   ├── dto/
  │   │   │   └── UserInfoDto.java
  │   │   ├── exception/
  │   │   │   └── GlobalExceptionHandler.java
  │   │   ├── service/
  │   │   │   └── UserService.java
  │   │   └── SsoApplication.java
  │   └── resources/
  │       ├── templates/
  │       │   ├── index.html
  │       │   └── dashboard.html
  │       ├── application.properties
  │       ├── application-dev.properties
  │       └── application-prod.properties
  └── test/
      └── java/com/orbit/sso/
          ├── controller/
          │   ├── ApiMeControllerTest.java
          │   └── AuthControllerTest.java
          ├── exception/
          │   └── GlobalExceptionHandlerTest.java
          ├── service/
          │   └── UserServiceTest.java
          └── SsoApplicationTests.java
  keycloak/
  └── realm-export.json
  docker-compose.yml
  Dockerfile


Tests

  Solo unitarios (no requieren servicios externos) en CMD:
    ./mvnw test -Dspring.profiles.active=dev -Dtest=UserServiceTest,AuthControllerTest,GlobalExceptionHandlerTest

  Todos los tests incluyendo integración (requiere docker compose up corriendo) en CMD:
    ./mvnw test -Dspring.profiles.active=dev

  Los tests de integración (ApiMeControllerTest) requieren que Keycloak esté levantado porque validan el flujo completo de autenticación contra el servidor de identidad real.


Cobertura:
  - Unitarios: `UserService`, `AuthController`, `GlobalExceptionHandler`
  - Integración: `ApiMeControllerTest` — verifica autenticación, respuesta del endpoint `/api/me` y logout

Realm y Client en Keycloak
  No es necesario configurar nada manualmente. Al levantar el proyecto con docker compose up --build, Keycloak importa automáticamente el realm sso-realm desde el archivo keycloak/realm-export.json, que incluye:

  El realm configurado
  El client sso-app con sus redirect URIs
  El usuario de prueba con sus credenciales

  Si se necesita verificar la configuración desde la consola de administración, pueden acceder a http://localhost:8180 con las credenciales de admin y explorar el realm sso-realm.


Dificultades encontradas

  - Spring Boot 4 / Spring Security 7: el `AuthenticationPrincipalArgumentResolver` no se registraba correctamente en contextos     `@WebMvcTest` sin una `SecurityFilterChain` activa. Se resolvió usando `@SpringBootTest` con `@AutoConfigureMockMvc` y el perfil `dev` para los tests de integración, aprovechando el Keycloak real corriendo localmente.
  - Realm export sin usuarios: Keycloak no exporta usuarios por defecto desde la UI. Se resolvió agregando el bloque `users` manualmente al `realm-export.json` para que el usuario de prueba se cree automáticamente al importar el realm.
  - RP-Initiated Logout: requiere configurar `OidcClientInitiatedLogoutSuccessHandler` con el `ClientRegistrationRepository` para que Keycloak invalide la sesión correctamente en el proveedor de identidad.
  - El desafio mas grande fue configurar para que keycloak y la app funcionen juntas sobre todo cuando el siguiente desarrollador debe de hacerlo clonando un repo, nunca utilice keycloak y fue desafiante pero bueno para mi experiencia.
  - El client-secret de Keycloak está definido en `application-dev.properties` y en `realm-export.json`. En un entorno productivo estas credenciales deberían gestionarse mediante variables de entorno o un gestor de secretos. Para esta prueba se optó por mantenerlas en el código para facilitar que el proyecto funcione con un solo docker compose up --build.