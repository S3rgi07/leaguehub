# LeagueHub

Aplicación Android en Jetpack Compose para consultar ligas, partidos, tablas, equipos y
perfiles, además de gestionar partidos desde el rol de organizador.

## Ejecutar el proyecto

1. Abrir la raíz del repositorio en Android Studio.
2. Sincronizar Gradle con JDK 25 y Android SDK 37.
3. Ejecutar la configuración `app` en un emulador o dispositivo Android 8.0 o posterior.

También se puede generar el APK con:

```powershell
.\gradlew.bat :app:assembleDebug
```

El APK de entrega está en `APK/LeagueHub-debug.apk`.

## Arquitectura

Las funcionalidades usan MVI: `Screen`, `Route`, `ViewModel`, `State` y `Event`. La navegación
se implementa con Navigation Compose y destinos type-safe `@Serializable`. Los repositorios
compartidos se exponen desde `LeagueHubApplication`.

La documentación del bloque Jugador está en `docs/INTEGRANTE-2.md` y la del bloque Match
Center/Organizador en `docs/integrante-3.md`.

## Servicios

- Google Maps: P09 abre la ubicación o las indicaciones mediante una URL segura y un Intent.
- Compartir de Android: P09 y P11 abren el selector del sistema con el resumen correspondiente.
- JSONPlaceholder: Retrofit consulta una publicación pública y muestra el anuncio remoto en Home,
  con manejo de error y reintento.
- Room/SQLite: `leaguehub.db` conserva cuentas locales, sesión, rol, liga, onboarding,
  equipos seguidos, apariencia, preferencias de avisos/ubicación, notificaciones leídas y
  cambios del partido de demostración del organizador. Los datos editables se separan por usuario.
- El registro crea una cuenta local y el login comprueba su correo normalizado y contraseña.
  Las contraseñas se guardan como hash PBKDF2 con sal aleatoria; no se guardan en texto plano.
- Inicio y Mi perfil observan la sesión activa. Una cuenta nueva empieza sin equipo, dorsal
  ni estadísticas personales. Abrir un jugador desde una plantilla sigue mostrando ese jugador.

## Datos y alcance actual

La base está en el almacenamiento privado de Android, en la ruta que devuelve
`context.getDatabasePath("leaguehub.db")` (normalmente
`/data/user/0/com.leaguehub.app/databases/leaguehub.db`). Room es la capa de acceso a SQLite;
no es memoria ROM y no hay un servidor de cuentas ni sincronización entre dispositivos.

Tablas:

| Tabla | Contenido |
|---|---|
| `user_accounts` | Cuentas, hash/sal de contraseña, rol, liga y onboarding por usuario |
| `user_session` | Sesión actualmente abierta |
| `user_data` | Estado editable local por `userId` y `dataKey`: equipos seguidos, apariencia, preferencias, notificaciones y flujo del organizador |

La versión 2 incluye migración desde la versión 1 y conserva su sesión. Los registros antiguos
no guardaban contraseña ni una cuenta recuperable: para volver a entrar después de cerrar esa
sesión hay que crear una cuenta una vez. Se retiró el acceso fijo de Sofía.

Para probar: crea dos cuentas desde Registro, cambia entre ellas cerrando sesión e iniciando con
cada correo/contraseña y verifica Inicio/Perfil. Sigue un equipo o cambia una preferencia,
reinicia la aplicación y vuelve a entrar: el cambio debe permanecer asociado a esa cuenta.

**Aún son demostraciones:** catálogos de ligas/equipos/jugadores, calendario, estadísticas,
noticias, predicciones, cámara/OCR y envío de correo. El organizador conserva localmente las
ediciones que permiten las pantallas actuales (marcador, árbitro, eventos, plantillas, pausa y
publicación) para el único flujo de partido compartido que ya tenía el proyecto; no es todavía
un CRUD de múltiples partidos ni publica en Internet. Los botones de edición de eventos/árbitro
mantienen sus acciones simplificadas; faltan formularios completos. Las preferencias de ubicación
y avisos se guardan, pero no sustituyen permisos de Android ni programan notificaciones reales.
No hay aún alta/edición de ligas, equipos o estadísticas de jugador. La recuperación por correo
informa que está pendiente, sin afirmar que se envió un código.

## Librerías

- Jetpack Compose y Material 3 para la interfaz.
- Navigation Compose con Kotlin Serialization para navegación type-safe.
- AndroidX Lifecycle, ViewModel y StateFlow para MVI.
- Room para persistencia local y Retrofit con Gson para la llamada HTTP.
- JUnit, Compose UI Test y Espresso para pruebas.

## Verificación

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:lintDebug
.\gradlew.bat :app:connectedDebugAndroidTest
```

La segunda línea requiere un emulador o dispositivo conectado.

## Avance en FeatureFinal

Se inició la fase local definitiva. Room v3 incorpora `leagues`, `clubs`, `players` y
`fixtures`, con relaciones y migración desde las versiones anteriores. CompetitionRepository
valida propietario, equipos de la misma liga, dorsal, fecha y marcador. Esta base aún no está
conectada a los formularios ni sustituye los fixtures de las pantallas.

Para continuar en otra máquina:

1. Clonar el repositorio y cambiar a `FeatureFinal`; actualizar con `git pull --ff-only`.
2. Configurar JDK 25 y Android SDK 37; `local.properties` es específico de cada máquina.
3. Ejecutar las verificaciones de este README.
4. Siguiente bloque: formularios MVI de ligas y equipos, accesibles desde el organizador;
   después jugadores y partidos. Usar CompetitionRepository y comprobar el propietario desde
   la sesión; evitar introducir otro catálogo paralelo en memoria.
5. Después conectar perfil, calendario y tabla con Room; calcular posiciones desde resultados,
   añadir eventos y estadísticas, y completar edición/eliminación con protección de relaciones.

Las cuentas y datos locales del emulador no viajan con Git. El APK en `APK/` corresponde al
último bloque de perfil validado; para probar el código más reciente generar el APK con Gradle.
Los cambios personales de Android Studio que permanezcan fuera de los commits deben conservarse.
