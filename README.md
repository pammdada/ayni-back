# AYNI backend

API REST modular para comunidades, catálogo, cadena de favores y emergencias.

## Inicio local

1. Copia `.env.example` como `.env` en esta carpeta y completa las variables de Supabase.
2. Ejecuta `./gradlew.bat bootRun` desde Windows.
3. Consulta `GET /actuator/health`.

Spring importa automáticamente `ayni-back/.env` mediante
`spring.config.import=optional:file:.env[.properties]`, así que no necesitas exportar
las variables en la terminal. En producción define esas mismas variables en el entorno
del servidor (el archivo `.env` no se despliega).

Variables principales:

- `SUPABASE_DB_URL`, `SUPABASE_DB_USER`, `SUPABASE_DB_PASSWORD`: conexión JDBC a PostgreSQL.
- `SUPABASE_JWT_ISSUER_URI`: issuer de los JWT de Supabase.
- `APP_SECURITY_JWT_ENABLED`: validación de JWT (debe ser `true` en producción).
- `DB_POOL_MAX_SIZE`, `DB_POOL_MIN_IDLE`: tamaño del pool Hikari (opcionales).

Las migraciones se gestionan exclusivamente con Flyway; Hibernate valida el esquema y no lo modifica.
Al arrancar, Flyway aplica automáticamente las migraciones pendientes:

- `V8__add_performance_indexes.sql`: índices para feed, perfiles y comunidades.
- `V9__seed_onboarding_tags.sql`: habilidades que muestra el onboarding.
- `V10__add_profile_sector.sql`: columna `sector` del perfil.

En producción define `APP_SECURITY_JWT_ENABLED=true` y `SUPABASE_JWT_ISSUER_URI`. Así Spring Security valida los JWT emitidos por Supabase. Para desarrollo local queda desactivado por defecto; no uses ese valor en un despliegue público.
