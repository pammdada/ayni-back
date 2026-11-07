# AYNI backend

API REST modular para comunidades, catálogo, cadena de favores y emergencias.

## Inicio local

1. Copia `.env.example` y define las variables de Supabase en tu terminal o IDE.
2. Ejecuta `./gradlew.bat bootRun` desde Windows.
3. Consulta `GET /actuator/health`.

Las migraciones se gestionan exclusivamente con Flyway; Hibernate valida el esquema y no lo modifica.

En producción define `APP_SECURITY_JWT_ENABLED=true` y `SUPABASE_JWT_ISSUER_URI`. Así Spring Security valida los JWT emitidos por Supabase. Para desarrollo local queda desactivado por defecto; no uses ese valor en un despliegue público.
